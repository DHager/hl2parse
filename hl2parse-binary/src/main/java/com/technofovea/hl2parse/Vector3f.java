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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utility class to represent 12-byte vectors
 * @author Darien Hager
 */
public class Vector3f {

    static final int BYTELEN = 4;
    static final int NUMVALS = 3;
    
    float x;
    float y;
    float z;

    /**
     * Creates a new object, attempting to draw from data in the given buffer.
     * @param b The buffer that data will be read from. The buffer's position
     * value will be changed when the constructor completes.
     * @throws BufferUnderflowException If the buffer does not have enough data availible.
     */
    public Vector3f(ByteBuffer b) {
        super();
        byte[] backing = new byte[NUMVALS*BYTELEN];
        b.get(backing);

        ByteBuffer temp = ByteBuffer.wrap(backing);
        temp.order(ByteOrder.LITTLE_ENDIAN);

        x = temp.getFloat();
        y = temp.getFloat();
        z = temp.getFloat();

    }

    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }

    public String toStringRounded() {
        return Math.round(x) + ", " + Math.round(y) + ", " + Math.round(z);
    }

    public byte[] toBytes() {
        byte[] backing = new byte[NUMVALS*BYTELEN];
        ByteBuffer b = ByteBuffer.wrap(backing);
        b.putFloat(x);
        b.putFloat(y);
        b.putFloat(z);
        return backing;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }



    
}
