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

import java.nio.ByteBuffer;

/**
 *
 * @author Darien Hager
 */
public abstract class BlobValue extends BlobNode {
    public enum DataType{
        TEXT(0),
        DWORD(1),
        RAW(2);

        public static DataType fromNumber(int typeNum){
            for(DataType t: DataType.values()){
                if(t.getNumericCode() == typeNum){
                    return t;
                }
            }
            return null;
        }
        public static DataType fromDescriptor(ByteBuffer meta) {
            int typeNum = meta.get();
            return fromNumber(typeNum);
        }

        private int num;
        private DataType(int num) {
            this.num = num;
        }
        public int getNumericCode(){
            return num;
        }


    }
    
    String name;

    public BlobValue(String name) {
        this.name = name;
    }
    
    public abstract DataType getType();

    public String getName() {
        return name;
    }

    




}
