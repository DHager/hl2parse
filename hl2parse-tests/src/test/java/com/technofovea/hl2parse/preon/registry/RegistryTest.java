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

import java.io.File;
import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.DefaultCodecFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Darien Hager
 */
public class RegistryTest {

    static final String CRBLOB = "C:/Program Files/Steam/ClientRegistry.blob";

    @Ignore
    @Test
    public void temp() throws Exception{
        DefaultCodecFactory fact = new DefaultCodecFactory();
        Codec<ItemSet> codec = fact.create(ItemSet.class);

        File src = new File(CRBLOB);
        ItemSet is = Codecs.decode(codec, src);
        //Item first = is.items.items.get(0);
        //System.out.println(first.descriptor.length);
    }
}
