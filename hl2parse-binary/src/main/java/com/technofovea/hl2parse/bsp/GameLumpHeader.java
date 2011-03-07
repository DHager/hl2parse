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

import com.technofovea.hl2parse.ParseUtil;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds metadata about a "game lump", not to be confused with a {@link BspLumpHeader},
 * game lumps are one more level of abstraction and are found within a BSP lump.
 * 
 * @author Darien Hager
 */
public class GameLumpHeader {

    private static final Logger logger = LoggerFactory.getLogger(GameLumpHeader.class);
    private int offset;
    private int length;
    private short version;
    private short flags;
    private final int id;

    /**
     * Attempts to create a new object from header data in the given bytebuffer.
     * @param lumpSection The buffer to be read from. On success, the buffer's position will have been advanced.
     * @throws BspParseException If an error occurred while processing.
     */
    public GameLumpHeader(ByteBuffer lumpSection) throws BspParseException {
        final int originalPosition = lumpSection.position();
        boolean resetPosition = true;
        try {
            id = lumpSection.getInt();
            flags = lumpSection.getShort();
            version = lumpSection.getShort();
            offset = lumpSection.getInt();
            length = lumpSection.getInt();

            if (offset < 0) {
                throw new BspParseException("Game lump header has negative offset");
            }
            if (length < 0) {
                throw new BspParseException("Game lump header has negative length");
            }

            if (logger.isDebugEnabled()) {
                String hexName = ParseUtil.toAscii(id, true);
                logger.debug("Game-lump created. ID {} ({}), version {}, offset {}, length {}", new Object[]{id, hexName, version, offset, length});
            }
            resetPosition = false; // Success, no need to reset
        } finally {
            if (resetPosition) {
                lumpSection.position(originalPosition);
            }
        }
    }

    /**
     * Turns a "nice" 4-character string name for a game lump into a game lump ID.
     *  The ID is little-endian, meaning "prps" is reversed before becoming "1936749168"
     * @param str String to convert, should be 4 characters or less.
     * @return The integer value, or -1 on error
     */
    public static int stringToId(String str) {
        if (str.length() > 4) {
            logger.error("Game-lump 'hex label' was greater than four digits: " + str);
            return -1;
        }
        StringBuilder sb = new StringBuilder(str);
        sb.reverse();
        byte[] bytes = sb.toString().getBytes();
        if (bytes.length < 4) {
            //TODO verify that it adds zero to the correct end of the byte array
            bytes = Arrays.copyOf(bytes, 4);
        }
        ByteBuffer temp = ByteBuffer.wrap(bytes);
        return temp.getInt();

    }

    /**
     * Get the length of the data this header refers to
     * @return The data length in bytes
     */
    public int getLength() {
        return length;
    }

    /**
     * Get the offset within the game-lump of the data this refers to
     * @return The offset in bytes
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Get the version of the game-lump
     * @return A version number
     */
    public short getVersion() {
        return version;
    }

    /**
     * Get any flags associated with this game-lump
     * @return Flags, represented as a single number
     */
    public short getFlags() {
        return flags;
    }

    /**
     * Get the game-lump's numeric ID
     * @return The ID
     */
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameLumpHeader other = (GameLumpHeader) obj;
        if (this.offset != other.offset) {
            return false;
        }
        if (this.length != other.length) {
            return false;
        }
        if (this.version != other.version) {
            return false;
        }
        if (this.flags != other.flags) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.offset;
        hash = 73 * hash + this.length;
        hash = 73 * hash + this.version;
        hash = 73 * hash + this.flags;
        hash = 73 * hash + this.id;
        return hash;
    }
}
