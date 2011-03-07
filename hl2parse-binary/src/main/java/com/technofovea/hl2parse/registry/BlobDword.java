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

import com.technofovea.hl2parse.ParseUtil;
import java.nio.ByteBuffer;

/**
 *
 * @author Darien Hager
 */
public class BlobDword extends BlobValue{

    byte[] dword;

    public BlobDword(String name, ByteBuffer realValue) {
        super(name);
        dword = new byte[4];
        realValue.get(dword);
    }

    @Override
    public DataType getType() {
        return DataType.DWORD;
    }

    public byte[] getDword() {
        return dword;
    }

    @Override
    public String toString() {
        return (getName()+":"+ParseUtil.toHex(dword));
    }





    

    
    

}
