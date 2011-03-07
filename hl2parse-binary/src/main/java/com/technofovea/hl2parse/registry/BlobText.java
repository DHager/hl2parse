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
public class BlobText extends BlobValue{

    String text;

    public BlobText(String name, ByteBuffer realValue) {
        super(name);
        text = RegParser.getText(realValue);
    }    

    @Override
    public DataType getType() {
        return DataType.TEXT;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return (getName()+":"+getText());
    }

    

}
