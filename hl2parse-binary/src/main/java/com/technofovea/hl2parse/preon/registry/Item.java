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
/*
 * 
 */

package com.technofovea.hl2parse.preon.registry;

import org.codehaus.preon.annotation.*;

/**
 *
 * @author Darien Hager
 */
public class Item {

    @BoundNumber(size="16")
    public int descriptorLen;

    @BoundNumber(size="32")
    public int payloadLen;

    @BoundList(size="descriptorLen")
    public byte[] descriptor;

    @BoundList(size="payloadLen")
    public byte[] payload;

    @Init
    public void temp(){
        System.out.println("");
    }
}
