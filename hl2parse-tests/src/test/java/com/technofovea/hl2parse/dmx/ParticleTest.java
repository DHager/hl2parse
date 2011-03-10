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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.codehaus.preon.Codec;
import org.junit.Test;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.CodecDecorator;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.DefaultCodecFactory;
import org.junit.Before;

public class ParticleTest {

    private CodecDecorator[] decorators;
    private DefaultCodecFactory fact;

    @Before
    public void setUp() {
        fact = new DefaultCodecFactory();
        decorators = new CodecDecorator[]{};
    }

    public <T> Codec<T> getCodec(Class<T> clazz) {
        return fact.create(clazz);
    }

    @Test
    public void shouldDecodeCorrectly() throws DecodingException, IOException, URISyntaxException {


        final File src = new File(this.getClass().getResource("cloud.pcf").toURI());

        DmxFile p = Codecs.decode(getCodec(DmxFile.class), src);

        /*
        System.out.println("ver:" + p.version);
        System.out.println("strs#:" + p.numStrings);
        for (String s : p.strings) {
            //sSystem.out.println(s.text);
        }
        System.out.println("elems#:" + p.numElements);
        for (DmxElement e : p.elements) {
            System.out.println(e);
            for (DmxAttribute a : e.attrs.items) {
                System.out.println("\t" + a);
            }
        }
         * 
         */
    }
}
