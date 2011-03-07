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
package com.technofovea.hl2parse.registry;

import com.technofovea.hl2parse.registry.CellItem;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Darien Hager
 */
public class CellCollection implements Iterable<CellItem>{
    List<CellItem> items = new ArrayList<CellItem>();
    int underflow = 0;

    public Iterator<CellItem> iterator() {
        return items.iterator();
    }


}
