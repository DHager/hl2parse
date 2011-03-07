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
package com.technofovea.hl2parse;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Miscellaneous utilities for parsing both binary and text-based files.
 * @author Darien Hager
 */
public class ParseUtil {

    /**
     * The null byte terminator used in C-style strings
     */
    public static final int NULL_TERMINATOR = 0x00;
    static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    /**
     * Given an integer value, reinterpret it as a string stored in four bytes.
     *
     * This is useful for a few places where developers have chosen integer IDs
     * which map to a human-readable mnemonic value.
     *
     * @param i The integer to interpret
     * @param reversed True for big-endian, false for little-endian
     * @return A string representation of the integer
     */
    public static String toAscii(int i, boolean reversed) {
        byte[] bytes = new byte[4];
        if (!reversed) {
            bytes[0] = (byte) (i >> 24);
            bytes[1] = (byte) ((i << 8) >> 24);
            bytes[2] = (byte) ((i << 16) >> 24);
            bytes[3] = (byte) ((i << 24) >> 24);
        } else {
            bytes[3] = (byte) (i >> 24);
            bytes[2] = (byte) ((i << 8) >> 24);
            bytes[1] = (byte) ((i << 16) >> 24);
            bytes[0] = (byte) ((i << 24) >> 24);
        }
        return new String(bytes);
    }

    /**
     * Given an array of bytes, return a string of hexadecimal characters.
     * @param bytes The byte array to interpret
     * @return A series of (upper-cased) hexadecimal characters, without any leading values such as 0x
     */
    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(HEX_CHARS[(b & 0xF0) >>> 4]);
            sb.append(HEX_CHARS[b & 0x0F]);
        }

        return sb.toString();
    }

    public static String toHex(byte b){
        return toHex(new byte[]{b});
    }

    /**
     * Interprets the bytes inside the given buffer as a hexadecimal string.
     *
     * The data in the buffer between its position and limit will be used, and the position will
     * be restored to its original value afterwards.
     *
     * @param buf The buffer to interpret, using the data between its position and limit.
     * @return A string of (upper-cased) hexadecimal, with no leading prefix (ex. 0x)
     */
    public static String toHex(ByteBuffer buf) {
        int orig = buf.position();
        StringBuilder sb = new StringBuilder();
        while (buf.remaining() > 0) {
            byte b = buf.get();
            sb.append(HEX_CHARS[(b & 0xF0) >>> 4]);
            sb.append(HEX_CHARS[b & 0x0F]);
        }

        buf.position(orig);
        return sb.toString();
    }

    /**
     * Reads a null-terminated string from the current position of the given bytebuffer.
     * @param bb ByteBuffer to read from
     * @param limit The maximum number of characters to read before giving up.
     * @return The ASCII string that was found
     */
    public static String readString(ByteBuffer bb, int limit) {
        int max = Math.min(limit, bb.remaining());
        byte[] name = new byte[max];
        bb.get(name);
        return readString(name, max);
    }

    /**
     * Reads a null-terminated string from the current position of the given byte array.
     * @param buf ByteBuffer to read from
     * @param limit The maximum number of characters to read before giving up.
     * @return The ASCII string that was found
     */
    public static String readString(byte[] buf, int limit) {
        int max = Math.min(limit, buf.length);
        int firstnull = max;
        for (int i = 0; i
                < buf.length; i++) {
            if (buf[i] == NULL_TERMINATOR) {
                firstnull = i;
                break;

            }
        }
        return new String(Arrays.copyOf(buf, firstnull));
    }

    /**
     * Maps the given file into memory and returns a representative buffer.
     * @param target The file to load
     * @return A read-only buffer of corresponding data in little-endian mode.
     * @throws IOException If an error occurred accessing or mapping the file.
     */
    public static MappedByteBuffer mapFile(File target) throws IOException {
        FileInputStream fos = new FileInputStream(target);
        FileChannel fc = fos.getChannel();
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, target.length());
        mbb.order(ByteOrder.LITTLE_ENDIAN);
        return mbb;
    }

    /**
     * Reads a series of bytes from the given buffer, turning them into a
     * little-endian bit-set. The ByteBuffer's position is advanced appropriately.
     *
     * Thus the three-byte series of  10000001,00001000,00000000 has set flags
     * at positions 0,7, and 11
     * @param bb ByteBuffer to read from
     * @param numbytes Number of bytes to read.
     * @return A new BitSet with the correct areas set/unset
     */
    public static BitSet readBitset(ByteBuffer bb, int numbytes) {
        byte[] barr = new byte[numbytes];
        
        bb.get(barr);
        BitSet ret = new BitSet(numbytes * 8);

        for (int i_byte = 0; i_byte < barr.length; i_byte++) {
            for (int i = 0; i < 8; i++) {
                int idx = i_byte*8 + i;
                boolean isSet = (barr[i_byte] & (1 << i) )>0;
                ret.set(idx,isSet);
            }
        }
        return ret;
    }
}
