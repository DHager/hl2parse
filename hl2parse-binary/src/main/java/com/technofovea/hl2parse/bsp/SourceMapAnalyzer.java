/**
 * Copyright (C) 2011 Darien Hager
 *
 * This code is part of the "HL2Parse" project, and is licensed under
 * a Creative Commons Attribution-ShareAlike 3.0 Unported License. For
 * either a summary of conditions or the full legal text, please visit:
 *
 * http://creativecommons.org/licenses/by-sa/3.0/
 *
 * Permissions beyond the scope of this license may be available
 * at http://technofovea.com/ .
 */
package com.technofovea.hl2parse.bsp;

import com.technofovea.hl2parse.OffsetBuffer;
import com.technofovea.hl2parse.ParseUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class layers on top of {@link BinaryBspAnalyzer} in order to provide access
 * to source-engine-specific data within maps.
 * 
 * @author Darien Hager
 */
public class SourceMapAnalyzer extends BinaryBspAnalyzer {

    protected static final int TRANSFER_BUF_SIZE = 5192;
    private static final Logger logger = LoggerFactory.getLogger(SourceMapAnalyzer.class);
    protected static final Charset CHARSET = Charset.forName("ASCII");
    protected static byte[] EXPECTED_HEADER = "VBSP".getBytes();
    /**
     * Source map versions supported
     */
    protected static int[] EXPECTED_VERSIONS = new int[]{19, 20};
    /**
     * Number of lumps expected
     */
    public static int NUM_LUMPS = 64;
    /**
     * The lump index which contains entity data
     */
    public static int LUMP_ENTITIES = 0;
    /**
     * The lump index which contains packed/embedded files
     */
    public static int LUMP_PAKFILE = 40;
    /**
     * The lump index containing file-offset information for materials, allowing
     * you to translate an in-map ID (the array index) to a String stored at the given offset.
     */
    public static int LUMP_MATERIALOFFSETS = 44;
    /**
     * The lump which contains human-readable names for materials
     */
    public static int LUMP_MATERIALNAMES = 43;
    /**
     * The expected length of prop-names (padded on the end with null-bytes)
     */
    public static int PROP_DICT_ENTRY_LEN = 128;
    /**
     * The bsp-format lump which contains the "game lumps"
     */
    public static int LUMP_SOURCESPECIFIC = 35;
    /**
     * The ID (in string form) identifying the game-lump for static props
     */
    public static String GAMELUMP_PROPS = "prps";
    /**
     * The ID (in string form) identifying the game-lump for detail props
     */
    public static String GAMELUMP_PROPS_DETAIL = "prpd";
    protected List<String> materialNames = new ArrayList<String>();
    protected String entData;
    protected OffsetBuffer gameLumpData;
    protected GameLumpHeader[] gameLumpHeaders;
    protected List<String> staticPropNames = new ArrayList<String>();
    protected HashMap<String, Set<Integer>> staticPropSkins;
    protected ZipFile packedZip = null;
    protected Map<String, File> extractedFiles = new HashMap<String, File>();

    /**
     * Creates a new analyzer to operate on the data in the given buffer.
     * @param bb The buffer containing map data, from the current position to the limit.
     * @throws BspParseException If an error occurs in parsing
     */
    public SourceMapAnalyzer(ByteBuffer bb) throws BspParseException {
        super(bb);
        try {
            loadGameLumps();

            loadTextures();
            loadEntityData();

            loadStaticPropData();
        } catch (BufferUnderflowException ex) {
            throw new BspParseException(ex);
        }


    }

    @Override
    protected byte[] getInitialHeader() {
        return EXPECTED_HEADER;
    }

    @Override
    protected int getNumLumps() {
        return NUM_LUMPS;
    }

    @Override
    protected boolean handlesVersion(int version) {
        for (int v : EXPECTED_VERSIONS) {
            if (v == version) {
                return true;
            }
        }
        return false;
    }

    private void loadTextures() {
        List<Integer> materialNameOffsets = new ArrayList<Integer>();

        logger.debug("Loading world brush textures from lump {}", LUMP_MATERIALOFFSETS);
        OffsetBuffer matOffsetZone = getLumpData(LUMP_MATERIALOFFSETS);
        while (matOffsetZone.getBuf().remaining() > 0) {
            materialNameOffsets.add(matOffsetZone.getBuf().getInt());
        }

        OffsetBuffer nameZone = getLumpData(LUMP_MATERIALNAMES);
        for (int offset : materialNameOffsets) {
            nameZone.getBuf().position(offset);
            String name = ParseUtil.readString(nameZone.getBuf(), 512);
            name = "materials/" + name;
            materialNames.add(sanitizePath(name));
        }

    }

    /**
     * Converts the data inside the given buffer into ASCII text,
     * stripping trailing null-bytes.
     * @param buf The buffer to read from (from position to limit)
     * @return A string of text
     */
    protected String bufToAscii(ByteBuffer buf) {

        CharBuffer cb = CHARSET.decode(buf.asReadOnlyBuffer());
        // Strip all trailing null bytes.
        cb.clear();
        char nullByte = 0x00;
        while (cb.remaining() > 0) {
            //charAt() in this case is relative to current position
            if (nullByte == cb.charAt(0)) {
                break; // Found null byte, break out
            }
            // Advance
            cb.position(cb.position() + 1);
        }
        cb.flip(); // Only handle area before the last point we visited
        String ret = cb.toString();
        assert (!ret.endsWith("\u0000"));
        return ret;
    }

    private void loadEntityData() {
        logger.debug("Loading entity data from lump {}", LUMP_ENTITIES);
        OffsetBuffer entZone = getLumpData(LUMP_ENTITIES);
        entData = bufToAscii(entZone.getBuf());
    }

    private void loadGameLumps() throws BspParseException {
        logger.debug("Loading lump {} containing game-lumps", LUMP_SOURCESPECIFIC);
        gameLumpData = getLumpData(LUMP_SOURCESPECIFIC);
        int lumpCount = gameLumpData.getBuf().getInt();
        logger.debug("Game lump count {}", lumpCount);
        gameLumpHeaders = new GameLumpHeader[lumpCount];
        for (int i = 0; i < lumpCount; i++) {
            logger.trace("Loading game-lump header #{}", i);
            GameLumpHeader hdr = new GameLumpHeader(gameLumpData.getBuf());
            gameLumpHeaders[i] = hdr;
        }
    }

    /**
     * Retrieves the header for a game-lump with the given ID, if present
     * @see GameLumpHeader#stringToId(java.lang.String) 
     * @param lumpId A game-lump numeric ID
     * @return A game-lump header or null if not found
     */
    protected GameLumpHeader getGameLump(int lumpId) {
        GameLumpHeader hdr = null;
        for (int i = 0; i < gameLumpHeaders.length; i++) {
            GameLumpHeader temp = gameLumpHeaders[i];
            if (temp.getId() == lumpId) {
                hdr = temp;
                break;
            }
        }
        if (hdr == null) {
            logger.error("Game-lump {} could not be found", lumpId);
        }
        return hdr;

    }

    /**
     * Retrieve the game-lump data associated with a game-lump integer ID
     * @see GameLumpHeader#stringToId(java.lang.String)
     * @param lumpId A game-lump numeric ID
     * @return A buffer, or null if not found.
     */
    protected OffsetBuffer getGameLumpData(int lumpId) {

        GameLumpHeader hdr = getGameLump(lumpId);
        if (hdr == null) {
            return null;
        }

        return new OffsetBuffer(gameLumpData.getParent(), hdr.getOffset(), hdr.getLength());
    }

    /**
     * Loads and interprets data about static prop names and skins.
     */
    protected void loadStaticPropData() {
        final int id = GameLumpHeader.stringToId(GAMELUMP_PROPS);
        logger.debug("Loading static prop data from gamelump {} ({})", GAMELUMP_PROPS, id);
        GameLumpHeader hdr = getGameLump(id);
        OffsetBuffer propLumpData = getGameLumpData(id);


        final int numDictEntries = propLumpData.getBuf().getInt();
        logger.debug("Propname dictionary entry count {}", numDictEntries);
        byte[] strBuf = new byte[PROP_DICT_ENTRY_LEN];
        for (int i = 0; i < numDictEntries; i++) {
            propLumpData.getBuf().get(strBuf);
            String name = ParseUtil.readString(strBuf, strBuf.length);
            name = sanitizePath(name);
            staticPropNames.add(name);
        }

        final int totalLeafEntries = propLumpData.getBuf().getInt();
        logger.debug("Prop leaf entry count {}", totalLeafEntries);
        for (int i = 0; i < totalLeafEntries; i++) {
            // We have no use for this data... yet?
            //TODO just advance the buffer instead of reading this
            int leaf = propLumpData.getBuf().getShort();
        }


        staticPropSkins = new HashMap<String, Set<Integer>>();

        // Now, the meat of the matter!
        final int propCount = propLumpData.getBuf().getInt();
        logger.debug("Prop usage count {}", propCount);
        for (int i = 0; i < propCount; i++) {
            logger.trace("Loading prop instance #{}", i);
            GamePropSection section = new GamePropSection(hdr, propLumpData.getBuf());

            int propNameIndex = section.getPropNameIndex();
            int skin = section.getSkin();
            if (propNameIndex < 0 || propNameIndex >= staticPropNames.size()) {
                logger.error("Prop instance specifies a name index ({}) which is invalid", propNameIndex);
            }
            String propName = staticPropNames.get(propNameIndex);
            logger.trace("Prop named '{}' ({}) with skin {} ", new Object[]{propName, propNameIndex, skin});


            if (staticPropSkins.containsKey(propName)) {
                staticPropSkins.get(propName).add(skin);
            } else {
                Set<Integer> skinsUsed = new HashSet<Integer>();
                skinsUsed.add(skin);
                staticPropSkins.put(propName, skinsUsed);
            }
        }

    }

    /**
     * Retrieves a ZipFile that contains details about files which have been
     * packed into this BSP. Note that these entries are read-only.
     * @return A ZipFile.
     * @throws IOException
     */
    public ZipFile getPackedFiles() throws IOException {
        synchronized (this) {
            if (packedZip == null) {
                logger.debug("Initializing temp file for extracting packed BSP data");
                File tempfile = File.createTempFile("packbsp_pak_", ".zip");
                tempfile.deleteOnExit();
                logger.debug("Temp file at: {}", tempfile.getAbsolutePath());
                tempfile.deleteOnExit();
                OffsetBuffer buf = getLumpData(LUMP_PAKFILE);
                FileChannel fc = new FileOutputStream(tempfile, false).getChannel();
                fc.write(buf.getBuf());
                fc.close();
                packedZip = new ZipFile(tempfile, ZipFile.OPEN_READ);
            }
        }
        return packedZip;


    }

    /**
     * Retrieves an File for the given packed file, if it exists. Note
     * that matches are case-insensitive. Files returned are marked for deletion
     * when the JVM exits.
     * 
     * @param path The path to the packed file, ex. materials/maps/test/cubemapdefault.vtf
     * @return A temporary File containing the extracted data, or null if not found.
     * @throws IOException If an error occurred trying to extract and save the data.
     */
    public File getPackedFile(String path) throws IOException {

        path = path.toLowerCase();
        if (extractedFiles.containsKey(path)) {
            logger.debug("Packed file {} was already extracted once, returning cached File reference", path);
            return extractedFiles.get(path);
        }

        ZipFile zf = getPackedFiles();
        ZipEntry found = null;
        Enumeration<? extends ZipEntry> en = zf.entries();
        while (en.hasMoreElements()) {
            ZipEntry entry = en.nextElement();
            if (entry.getName().equalsIgnoreCase(path)) {
                logger.debug("Packed file found in zip data: ", entry.getName());
                found = entry;
                break;
            }
        }
        if (found == null) {
            logger.debug("Packed file {} could not be found in zip data.", path);
            return null;
        }

        //TODO change suffix to match entry name suffix?
        File tempfile = File.createTempFile("packbsp_extracted_", ".file");
        tempfile.deleteOnExit();
        InputStream is = zf.getInputStream(found);
        FileOutputStream fos = new FileOutputStream(tempfile);
        byte[] buffer = new byte[TRANSFER_BUF_SIZE];
        int numRead = 0;
        while (numRead > -1) {
            fos.write(buffer, 0, numRead);
            numRead = is.read(buffer);
        }
        fos.close();
        logger.debug("Packed file {} extracted to location: {}", path, tempfile.getAbsolutePath());

        extractedFiles.put(path, tempfile);
        return tempfile;


    }

    /**
     * Get a set of all brush-textures being used in the map. This may include
     * tool-textures that the user never sees, such as nodraw.
     * @return A set of relative texture paths
     */
    public Set<String> getBrushTextures() {
        return new HashSet(materialNames);
    }

    /**
     * Get the entity-data for the map.
     * @return A string of entity data.
     */
    public String getEntData() {
        return entData;
    }

    /**
     * Retrieve a mapping of prop-names (actually relative paths) to a set of
     * skin IDs which they use.
     * @return A mapping where each key is a prop-path, and each value is a set
     * of skins that are referenced for that prop.
     */
    public HashMap<String, Set<Integer>> getStaticPropSkins() {
        return staticPropSkins;
    }

    /**
     * Sanitizes a path, turning into lowercase and using forward-slashes
     * @param path The path to sanitize
     * @return The cleaned-up result
     */
    static String sanitizePath(String path) {
        path = path.toLowerCase().trim();
        path.replace("\\", "/");
        return path;
    }
}
