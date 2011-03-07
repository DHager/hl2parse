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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * This class manages a buffer which is a slice from a parent buffer. The main
 * benefit is that it remembers its own offset, which is useful for debugging.
 *
 * Note that this class sets the resulting child buffers to little-endian.
 * @author Darien Hager
 */
public class OffsetBuffer {

    /**
     * The parent buffer
     */
    protected ByteBuffer parent;
    /**
     * The offset in the parent at which the child buffer begins
     */
    protected int parentOffset;
    /**
     * The created child buffer which is a view of the parent
     */
    protected ByteBuffer buf;


    /**
     * Creates and manages a new sliced "view" of a buffer.
     * 
     * @param parent The parent buffer to create a sliced view of.
     * @param parentOffset The offset at which to start the slice
     * @param len The length of the slice
     */
    public OffsetBuffer(ByteBuffer parent, int parentOffset, int len) {
        this.parent = parent;
        this.parentOffset = parentOffset;

        int pos = parent.position();
        int lim = parent.limit();

        parent.position(parentOffset);
        parent.limit(parentOffset+len);
        this.buf = parent.slice();
        parent.position(pos);
        parent.limit(lim);

        this.buf.order(ByteOrder.LITTLE_ENDIAN);

    }

    

    /**
     * Get the child-buffer being managed by this object. This buffer
     * will generally be in little-endian mode.
     * @return A buffer which is a view of the parent
     */
    public ByteBuffer getBuf() {
        return buf;
    }

    /**
     * Gets the original parent buffer
     * @return The parent buffer
     */
    public ByteBuffer getParent() {
        return parent;
    }

   /**
    * Retrieves the offset in the parent buffer at which the child buffer begins.
    * @return A position within the parent buffer
    */
    public int getParentOffset() {
        return parentOffset;
    }
    /**
     * Retrieves the position in the parent buffer which corresponds to
     * the current position of the child buffer, taking the offset into account.
     * @return A position in the parent buffer
     */
    public int getAbsolutePosition(){
        return parentOffset + buf.position();
    }


}
