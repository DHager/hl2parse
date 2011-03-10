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

import com.technofovea.hl2parse.ParseUtil;
import java.util.Arrays;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundString;

/**
 * From a file-organization standpoint, DMX elements exist primarily as "places"
 * for attributes to be attached. The attributes are what contain most of the
 * useful information in the file format.
 *
 * @author Darien Hager
 */
public class DmxElement{
    protected DmxFile parent = null;
    protected int parentIndex = -1;
    protected AttrList attrs = new AttrList();


    @BoundNumber(size = "16")
    protected int typeNameIndex = -1;

    @BoundString
    protected String name;
    @BoundList(size = "16")
    private byte[] guid;

    @Override
    public String toString() {
        final String typedescription;
        final String typeName = getTypeName();
        if(typeName != null){
            typedescription = "type="+typeName;
        }else{
            typedescription = "typeidx="+typeNameIndex;
        }
        return "{element "+typedescription+",name=" + name + ",guid=" + ParseUtil.toHex(guid) + "}";
    }

    public String getName(){
        return name;
    }

    public String getTypeName(){
        if(parent==null || this.typeNameIndex <0 ){
            return null;
        }
        return parent.strings[this.typeNameIndex];
    }

    public byte[] getGUID(){
        return Arrays.copyOf(guid,guid.length);
    }

    public DmxAttribute[] getAttributes(){
         if(parent==null || this.parentIndex <0 ){
            return null;
        }
        return parent.attrs[parentIndex].items;
    }

    
}
