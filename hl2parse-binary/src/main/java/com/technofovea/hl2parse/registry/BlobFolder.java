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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Darien Hager
 */
public class BlobFolder extends BlobNode{

    public enum FolderItemType{
        FOLDER(1),
        FILE(2);

        public static FolderItemType fromNumber(int typeNum){
            for(FolderItemType t: FolderItemType.values()){
                if(t.getNumericCode() == typeNum){
                    return t;
                }
            }
            return null;
        }
        public static FolderItemType fromDescriptor(ByteBuffer meta) {
            int typeNum = meta.get();
            return fromNumber(typeNum);
        }

        private int num;
        private FolderItemType(int num) {
            this.num = num;
        }
        public int getNumericCode(){
            return num;
        }

    }


    String name;
    Map<String,BlobFolder> folders = new HashMap<String, BlobFolder>();
    Map<String,BlobValue> values = new HashMap<String, BlobValue>();
    public BlobFolder(String name) {
        this.name=name;
    }

    public Map<String, BlobFolder> getFolders() {
        return folders;
    }

    public Map<String, BlobValue> getValues() {
        return values;
    }    

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{"+getName()+":"+values.toString()+","+folders.toString()+"}";
    }

    
}
