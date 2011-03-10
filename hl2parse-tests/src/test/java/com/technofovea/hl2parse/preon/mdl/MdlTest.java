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

package com.technofovea.hl2parse.preon.mdl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Test;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.DefaultCodecFactory;

public class MdlTest {

    @Test
    public void shouldDecodeCorrectly() throws DecodingException, IOException, URISyntaxException {

        final DefaultCodecFactory fact = new DefaultCodecFactory();

        final File src = new File(this.getClass().getResource("buoy_ref.mdl").toURI());

        Codec<Mdl> codec = fact.create(Mdl.class);

        Mdl model = Codecs.decode(codec,src);
        System.out.println(model.magicHeader);
        System.out.println(model.version);
    }


}
