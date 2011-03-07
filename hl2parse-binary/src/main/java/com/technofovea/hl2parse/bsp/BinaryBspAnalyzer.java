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
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a base for writing classes which interpret the venerable BSP map format.
 * @todo Refactor into factory methods
 * @author Darien Hager
 */
public abstract class BinaryBspAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(BinaryBspAnalyzer.class);
    /**
     * The buffer (probably mapped) which represents the whole file data.
     * Should be read-only and little-endian.
     */
    protected ByteBuffer coreBuffer;
    /**
     * The version code found within the map
     */
    protected int version;
    /**
     * Header data for each of the BSP lumps
     */
    protected BspLumpHeader[] lumps;

    /**
     * Creates a new analyzer against the given bytebuffer.
     * @param bb The data to analyze, from the current position to the limit
     * @throws BspParseException If there were any errors parsing
     */
    public BinaryBspAnalyzer(ByteBuffer bb) throws BspParseException {
        logger.debug("Processing map data ({} bytes)", bb.remaining());
        
        coreBuffer = bb.asReadOnlyBuffer();
        coreBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        try {
            loadBasic();
        } catch (BufferUnderflowException bue) {
            throw new BspParseException(bue);
        }
    }

    private void loadBasic() throws BspParseException {
        final int num_lumps = getNumLumps();
        final byte[] expected_hdr = getInitialHeader();
        lumps = new BspLumpHeader[num_lumps];
        byte[] hdr = new byte[expected_hdr.length];

        coreBuffer.get(hdr);
        if (!Arrays.equals(hdr, expected_hdr)) {
            String hexHdr = ParseUtil.toHex(hdr);
            String hexExpected = ParseUtil.toHex(expected_hdr);
            throw new BspParseException("File does not begin with expected header. Expected "+hexExpected+" but got "+hexHdr);
        }

        int versionNumber = coreBuffer.getInt();
        logger.debug("Map version is {}", versionNumber);
        if (!handlesVersion(versionNumber)) {
            throw new BspParseException("Cannot handle file format version " + versionNumber);
        }
        version = versionNumber;

        for (int i = 0; i < getNumLumps(); i++) {
            logger.trace("Loading lump header #{}", i);
            lumps[i] = new BspLumpHeader(coreBuffer);
        }



    }

    /**
     * Get a buffer containing the payload data for the given lump.
     * @param lumpNum The lump to retrieve
     * @return The data, or null if the given lump does not exist
     */
    protected OffsetBuffer getLumpData(int lumpNum) {
        if (lumpNum < 0 || lumpNum >= lumps.length) {
            logger.error("BSP lump {} was out-of-range and could not be found", lumpNum);
            return null;
        }
        BspLumpHeader l = lumps[lumpNum];

        return new OffsetBuffer(coreBuffer, l.getOffset(), l.getLength());
    }

    /**
     * Checks whether a given BSP version is supported by this analyzer
     * @param version The version number to check
     * @return True if supported, false otherwise
     */
    protected abstract boolean handlesVersion(int version);

    /**
     * Get the number of lumps expected in this file
     * @return The number of lumps which are supported
     */
    protected abstract int getNumLumps();

    /**
     * Get the expected header for the file
     * @return The bytes expected
     */
    protected abstract byte[] getInitialHeader();
}
