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
package com.technofovea.hl2parse.preon.mdl;

import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.buffer.ByteOrder;

public class Image {

    @BoundNumber(byteOrder= ByteOrder.BigEndian)
    private int height;

    @BoundNumber(byteOrder= ByteOrder.BigEndian)
    private int width;

    @BoundList(size = "height*width")
    private Color[] pixels;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Color[] getPixels() {
        return pixels;
    }

}
