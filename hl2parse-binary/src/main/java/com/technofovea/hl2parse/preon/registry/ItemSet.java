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
package com.technofovea.hl2parse.preon.registry;

import java.util.List;
import org.codehaus.preon.annotation.*;
import org.codehaus.preon.buffer.ByteOrder;

/**
 *
 * @author Darien Hager
 */

@TypePrefix(size=16,value="20481")
public class ItemSet {

    @BoundNumber(size="16")
    public int header;

    @BoundNumber(size="32")
    public int childLen;

    @Bound
    public int nullPadding;

    @Slice(size="childLen-(2+4+4)")
    @BoundList(type=Item.class)
    public List<Item> items;

    @Slice(size="nullPadding")
    @BoundList
    private byte[] padding;

}
