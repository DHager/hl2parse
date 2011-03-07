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
package com.technofovea.hl2parse.mdl;

import com.technofovea.hl2parse.Vector3f;
import com.technofovea.hl2parse.ParseUtil;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.technofovea.hl2parse.mdl.ModelSection.*;

/**
 * File format details are from Valve's studio.h header from 2005
 * @author Darien Hager
 */
public class ModelData {

    private static final Logger logger = LoggerFactory.getLogger(ModelData.class);
    Map<ModelSection, Integer> indexes = new HashMap<ModelSection, Integer>();
    Map<ModelSection, Integer> contentCounts = new HashMap<ModelSection, Integer>();
    ByteBuffer bb;
    int id = -1;
    int version = -1;
    int checksum = 0;
    String modelName = null;
    int dataLength = 0;
    BitSet flags = null; // See ModelFlags
    List<String> texturePaths = new ArrayList<String>();
    List<String> textureNames = new ArrayList<String>();
    Map<Integer, List<Integer>> skinTable = new HashMap<Integer, List<Integer>>();
    List<String> includeModels = new ArrayList<String>();
    private Vector3f eyeposition;
    private Vector3f illumposition;
    private Vector3f hull_min;
    private Vector3f hull_max;
    private Vector3f view_bbmin;
    private Vector3f view_bbmax;
    private float mass;
    private int contents;
    private int numskinfamilies;

    public ModelData(ByteBuffer bb) throws ModelParseException {
        this.bb = bb.slice();
        this.bb.order(ByteOrder.LITTLE_ENDIAN);
        logger.debug("Parsing model data, {} remaining", this.bb.remaining());
        try {
            load();
            fixPaths();
        } catch (BufferUnderflowException bue) {
            throw new ModelParseException(bue);
        }
    }

    private void load() {
        assert (bb.position() == 0);
        id = readInt();
        version = readInt();
        checksum = readInt();
        modelName = readString(64);
        dataLength = readInt();

        logger.debug("ID is {} ({}), version is {}, name is {}", new Object[]{ParseUtil.toAscii(id, true), id, version, modelName});

        eyeposition = new Vector3f(bb);
        illumposition = new Vector3f(bb);
        hull_min = new Vector3f(bb);
        hull_max = new Vector3f(bb);
        view_bbmin = new Vector3f(bb);
        view_bbmax = new Vector3f(bb);
        flags = ParseUtil.readBitset(bb, 4);

        loadCountAndIndex(BONE);
        loadCountAndIndex(BONECONTROLLER);
        loadCountAndIndex(HITBOXSET);
        loadCountAndIndex(LOCALANIM);
        loadCountAndIndex(LOCALSEQ);

        BitSet activitylistversion = ParseUtil.readBitset(bb, 4);
        int eventsindexed = readInt();

        loadCountAndIndex(TEXTURE);
        loadCountAndIndex(CDTEXTURE);

        loadCount(SKIN); // Num skin refs
        numskinfamilies = readInt();
        loadIndex(SKIN);

        loadCountAndIndex(BODYPART);
        loadCountAndIndex(LOCALATTACHMENT);

        int numlocalnodes = readInt();
        loadIndex(LOCALNODE);
        loadIndex(LOCALNODENAME);

        loadCountAndIndex(FLEXDESC);
        loadCountAndIndex(FLEXCONTROLLER);
        loadCountAndIndex(FLEXRULE);
        loadCountAndIndex(IKCHAIN);
        loadCountAndIndex(MOUTH);
        loadCountAndIndex(LOCALPOSEPARAM);
        loadIndex(SURFACEPROP);

        // Weird, this one's in reverse order, index then count
        loadIndex(KEYVALUE);
        loadCount(KEYVALUE);

        loadCountAndIndex(LOCALIKAUTOPLAYLOCK);
        mass = readFloat();
        contents = readInt();

        loadCountAndIndex(INCLUDEMODEL);
        int virtualModel = readInt(); // Throwaway pointer space
        loadIndex(SZANIMBLOCKNAME);

        loadCountAndIndex(ANIMBLOCK);
        int animblockModel = readInt(); // Throwaway pointer space
        loadIndex(BONETABLEBYNAME);
        int pVertexBase = readInt(); //Throwaway pointer space
        int pIndexBase = readInt(); //Throwaway pointer space

        byte constdirectionallightdot = readByte();
        byte rootLOD = readByte();
        byte numAllowedRootLODs = readByte();

        byte unused = readByte();
        int zero_frame_cache_index = readInt();

        loadCountAndIndex(FLEXCONTROLLERUI);
        int[] unused3 = readIntArray(2);
        loadIndex(STUDIOHDR2);
        int[] unused4 = readIntArray(1);

        assert (bb.position() == 408);



        //TEMP
        int[] idxList = new int[indexes.size()];
        ArrayList<Integer> temp = new ArrayList<Integer>(indexes.values());
        Collections.sort(temp);
        for (int i = 0; i < temp.size(); i++) {
            idxList[i] = temp.get(i).intValue();
        }



        /*
         * Now that we've parsed the header and its various offsets and counts,
         * go ahead and load the data we're interested in.
         */

        if (indexes.get(STUDIOHDR2) > 0) {
            //TODO
        }

        int pos = bb.position();

        loadMaterialPaths();
        bb.position(pos);

        loadMaterialNames();
        bb.position(pos);

        loadSkinTable();
        bb.position(pos);

        loadIncludeModels();
        bb.position(pos);

        loadBodyParts();
        bb.position(pos);
    }

    private int readShort() {
        return bb.getShort();
    }

    private int readInt() {
        return bb.getInt();
    }

    private void loadCountAndIndex(ModelSection modelSection) {
        loadCount(modelSection);
        loadIndex(modelSection);
    }

    private void loadIndex(ModelSection modelSection) {
        indexes.put(modelSection, bb.getInt());
    }

    private void loadCount(ModelSection modelSection) {
        contentCounts.put(modelSection, bb.getInt());
    }

    private byte[] readBytes(int count) {
        byte[] ret = new byte[count];
        bb.get(ret);
        return ret;
    }

    private byte readByte() {
        return bb.get();
    }

    private float readFloat() {
        return bb.getFloat();
    }

    private String readString(int len) {
        return ParseUtil.readString(bb, len);
    }

    private int[] readIntArray(int size) {
        int[] ret = new int[size];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = bb.getInt();
        }
        return ret;
    }

    public String getModelName() {
        return fixPath(modelName);
    }

    public boolean checkModelFlag(ModelFlag m) {
        return flags.get(m.getBitIndex());
    }

    static String fixPath(String origPath) {
        return origPath.replace("\\", "/");
    }

    public List<String> getTextureSearchPaths() {
        return Collections.unmodifiableList(texturePaths);
    }

    public List<String> getTextureNames() {
        return Collections.unmodifiableList(textureNames);
    }

    public int getSkinCount() {
        return numskinfamilies;
    }

    public List<String> getTexturesForSkin(int skinId) {
        if (!skinTable.containsKey(skinId)) {
            logger.error("Skin {} does not exist", skinId);
            return new ArrayList<String>();
        }
        List<String> ret = new ArrayList<String>();
        for (int texId : skinTable.get(skinId)) {
            //TODO guard against bad texture ID
            String matName = textureNames.get(texId);
            ret.add(matName);
        }
        return ret;
    }

    public List<String> getIncludedModels() {
        return Collections.unmodifiableList(includeModels);
    }

    private void loadSkinTable() {
        // inline func pSkinref
        bb.position(indexes.get(SKIN));
        logger.trace("Loading skin-family replacement tables at offset {}", bb.position());
        for (int i = 0; i < numskinfamilies; i++) {
            List<Integer> targets = new ArrayList<Integer>();
            skinTable.put(i, targets);
            for (int j = 0; j < this.contentCounts.get(SKIN); j++) {
                int indexVal = readShort();
                targets.add(indexVal);
            }
        }
    }

    private void loadMaterialPaths() {
        // inline func pCdtexture
        bb.position(indexes.get(CDTEXTURE));
        logger.trace("Loading material paths at offset {} ", bb.position());
        List<Integer> pathIndexes = new ArrayList<Integer>();
        for (int i = 0; i < this.contentCounts.get(CDTEXTURE); i++) {
            int stringStartIndex = readInt();
            pathIndexes.add(stringStartIndex);
        }

        for (int i : pathIndexes) {
            bb.position(i);
            String path = readString(256);
            texturePaths.add(path);
        }

    }

    private void loadMaterialNames() {
        // struct mstudiotexture_t
        bb.position(indexes.get(TEXTURE));
        logger.trace("Loading material names at offset {}", bb.position());
        for (int i = 0; i < this.contentCounts.get(TEXTURE); i++) {

            int blockStartPos = bb.position();

            int relativeNameOffset = readInt();
            int lastPos = bb.position();
            bb.position(blockStartPos + relativeNameOffset);
            String name = readString(128);
            bb.position(lastPos);

            textureNames.add(name);

            int flags = readInt();
            int used = readInt();
            int unused1 = readInt();
            int matPtr = readInt();
            int clientMatPtr = readInt();
            int[] unused2 = readIntArray(10);



        }
    }

    private void loadIncludeModels() {
        // struct mstudiotexture_t
        bb.position(indexes.get(INCLUDEMODEL));
        logger.trace("Loading {} include models at offset {}", contentCounts.get(INCLUDEMODEL), bb.position());
        for (int i = 0; i < this.contentCounts.get(INCLUDEMODEL); i++) {
            int blockStartPos = bb.position();
            int descNameOffset = readInt();
            int fileNameOffset = readInt();
            int endPos = bb.position();


            bb.position(blockStartPos + descNameOffset);
            String descName = readString(128);
            bb.position(blockStartPos + fileNameOffset);
            String filename = readString(128);

            includeModels.add(filename);
            bb.position(endPos);


        }


    }

    //mstudiobodyparts_t
    private void loadBodyParts() {
        final int startPos = indexes.get(BODYPART);
        final int count = contentCounts.get(BODYPART);
        logger.trace("Loading {} bodyparts at offset {}", count, startPos);

        bb.position(startPos);
        for (int i = 0; i < count; i++) {
            final int structStartPos = bb.position();

            final int name_offset = readInt();
            final int numModels = readInt();
            final int base = readInt();
            final int model_offset = readInt();



            int prev = bb.position();
            bb.position(structStartPos + name_offset);
            final String bodypartName = readString(256);

            bb.position(structStartPos + model_offset);
            //TODO model
            bb.position(prev);


        }

    }

    private void fixPaths() {
        List<String> newPaths = new ArrayList<String>();
        for (String p : texturePaths) {
            // Correct slash types
            String fixed = p.replace("\\", "/");
            // Strip leading slashes
            fixed = fixed.replaceFirst("^/+", "");
            // Prepend materials
            fixed = "materials/" + fixed;
            // Ensure a trailing slash is present
            if (!fixed.endsWith("/")) {
                fixed += "/";
            }
            newPaths.add(fixed);
        }
        texturePaths = newPaths;

        List<String> newNames = new ArrayList<String>();
        for (String n : textureNames) {
            // Correct slash types
            String fixed = n.replace("\\", "/");
            // Remove leading slashes
            fixed = fixed.replaceAll("^/+", "");
            // Suffix
            fixed += ".vmt";
            newNames.add(fixed);
        }
        textureNames = newNames;
    }
}
