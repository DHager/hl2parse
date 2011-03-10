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
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundObject;
import org.codehaus.preon.annotation.Choices;
import org.codehaus.preon.annotation.Init;

/**
 *
 * @author Darien Hager
 */
public class DmxAttribute {

    protected DmxFile parent;

    @BoundNumber(size="16")
    protected int typeNameIndex;

    @Bound
    protected byte contentTypeCode;

    protected DmxAttrDataType contentType;

    @BoundObject(selectFrom=@Choices(alternatives={
        @Choices.Choice(condition="contentTypeCode == 0x01",type=ElementOffset.class),
        @Choices.Choice(condition="contentTypeCode == 0x0f",type=ElementArray.class)
    }))
    protected Placeholder content;

    @Override
    public String toString() {
        final String typedescription;
        final String typeName = getTypeName();
        if(typeName != null){
            typedescription = "type="+typeName;
        }else{
            typedescription = "typeidx="+typeNameIndex;
        }
        return "{element "+typedescription+",code=" + ParseUtil.toHex(contentTypeCode) + ", content="+content.toString()+"}";
    }

    //@Init
    public void init(){
        for(DmxAttrDataType t: DmxAttrDataType.values()){
            if(t.getTypeCode() == contentTypeCode){
                contentType = t;
                break;
            }
        }
        assert(contentType != null);
        assert(contentType.getStorageClass().equals(content.getClass()));

    }

    public String getTypeName() {
        if(parent==null || this.typeNameIndex <0 ){
            return null;
        }
        return parent.strings[this.typeNameIndex];
    }




}
