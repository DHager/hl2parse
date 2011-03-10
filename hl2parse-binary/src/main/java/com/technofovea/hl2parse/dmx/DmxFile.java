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
package com.technofovea.hl2parse.dmx;

import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundObject;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.annotation.Init;

/**
 * A DMX file is designed to hierarchically store typed data, using a tree of
 * elements which each may have attributes, storing scalars or other elements.
 *
 * In terms of binary file organization, the tree is serialized something like:
 *
 * Element A
 * Element B
 * Element C
 * Attribute A-1
 * Attribute A-2
 * Attribute B-1
 * Attribute C-1
 * Attribute C-2
 * Attribute C-3
 * 
 * @author Darien Hager
 */
public class DmxFile {

    /**
     * Null-terminated version string, ex: <!-- dmx encoding binary XXX format pcf XXX -->
     */
    @BoundString
    protected String version;
    @BoundNumber(size = "16")
    /**
     * Number of strings in dictionary
     */
    protected int numStrings;
    /**
     * Dictionary strings
     */
    @BoundString
    @BoundList()
    protected String[] strings;
    /**
     * Number of elements
     */
    @BoundNumber(size = "32")
    protected int numElements;
    /**
     * Elements stored
     */
    @BoundList(size = "numElements")
    protected DmxElement[] elements;
    /**
     * In-order corresponding top-level attribute-lists for each Element
     */
    @BoundList(size = "numElements")
    protected AttrList[] attrs;

    @Init
    protected void init() {
        for (int i = 0; i < elements.length; i++) {
            elements[i].parent = this;
            elements[i].parentIndex = i;
        }
        for (AttrList asets : attrs) {
            for (DmxAttribute a : asets.items) {
                a.parent = this;
            }
        }
    }
}
