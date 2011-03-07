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

import com.technofovea.hl2parse.ParseUtil;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interprets a PHY file.
 * @author Darien Hager
 */
public class PhyData {

    protected static final Charset CHARSET = Charset.forName("ASCII");
    private static final Logger logger = LoggerFactory.getLogger(PhyData.class);
    ByteBuffer bb;
    int id = -1;
    int checksum = 0;
    int solidCount = 0;
    ByteBuffer[] collisionSections = null;
    String propData = null;

    public PhyData(ByteBuffer bb) throws PhyParseException {
        this.bb = bb;
        bb.order(ByteOrder.LITTLE_ENDIAN);
        logger.debug("Parsing phy data, {} remaining", this.bb.remaining());
        try {
            load();
        } catch (BufferUnderflowException bue) {
            throw new PhyParseException(bue);
        }
    }

    private void load() {

        int hdrsize = readInt();
        id = readInt();
        solidCount = readInt();
        checksum = bb.getInt();

        int extra = (hdrsize) - bb.position();
        if (extra > 0) {
            bb.position(bb.position() + extra);
        }

        collisionSections = new ByteBuffer[solidCount];

        for (int i = 0; i < collisionSections.length; i++) {
            int len = bb.getInt();
            int oldLimit = bb.limit();
            bb.limit(bb.position() + len);
            ByteBuffer sectionData = bb.slice();
            sectionData.order(ByteOrder.LITTLE_ENDIAN);

            bb.position(bb.limit());
            bb.limit(oldLimit);
            collisionSections[i] = sectionData;
        }
        propData = ParseUtil.readString(bb, bb.remaining());
        //propData = cb.toString();




    }

    private int readShort() {
        return bb.getShort();
    }

    private int readInt() {
        return bb.getInt();
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


    public int getChecksum() {
        return checksum;
    }

    public ByteBuffer[] getCollisionSections() {
        return collisionSections;
    }

    public int getId() {
        return id;
    }

    public String getPropData() {
        return propData;
    }

    public int getSolidCount() {
        return solidCount;
    }
}
