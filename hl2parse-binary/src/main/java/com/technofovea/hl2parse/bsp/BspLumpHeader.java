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

import java.nio.ByteBuffer;

/**
 * Stores header information for a BSP lump
 * @author Darien Hager
 */
public class BspLumpHeader {

    //TODO handle the reordering in Version 21 L4d
    /**
     * The length of the lump identifier bytes
     */
    protected static int IDENT_LEN = 4;
    /**
     * The offset of the game lump within the data
     */
    protected int offset;
    /**
     * The length of the game lump
     */
    protected int length;
    /**
     * The version number for the game lump
     */
    protected int version;
    /**
     * The identity code given to this lump
     */
    protected byte[] identCode = new byte[IDENT_LEN];

    /**
     * Creates a new BspLumpHeader, drawing from the given buffer
     * @param lumpData The buffer to be read from. On success, the buffer's position will have been advanced.
     * @throws BspParseException If the lump header is invalid.
     */
    public BspLumpHeader(ByteBuffer lumpData) throws BspParseException {
        final int originalPosition = lumpData.position();
        boolean resetPosition = true;

        try {
            offset = lumpData.getInt();
            length = lumpData.getInt();
            version = lumpData.getInt();
            lumpData.get(identCode);

            if (offset < 0) {
                throw new BspParseException("Lump header has negative offset");
            }
            if (length < 0) {
                throw new BspParseException("Lump header has negative length");
            }
            resetPosition = false; // Success, no need to reset

        } finally {
            if (resetPosition) {
                lumpData.position(originalPosition);
            }
        }
    }

    /**
     * Retrieves the lump identifier bytes
     * @return A series of bytes of length {@link #IDENT_LEN}
     */
    public byte[] getIdentCode() {
        return identCode;
    }

    /**
     * Get the length of the data this header refers to
     * @return The data length in bytes
     */
    public int getLength() {
        return length;
    }

    /**
     * Get the offset of the data this header refers to, relative
     * to the start of the BSP file
     * @return The offset at which this lump's data begins
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Get the version number associated with this lump
     * @return A version number
     */
    public int getVersion() {
        return version;
    }
}
