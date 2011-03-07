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
package com.technofovea.hl2parse.xml;

import com.technofovea.hl2parse.vdf.ParseTest;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Darien Hager
 */
public class XmlTest {

    @Test
    public void go() throws Exception {
        InputStream is = ParseTest.class.getResourceAsStream("DefaultMaterials.xml");
        Assert.assertNotNull(is);
        JAXBContext jc = JAXBContext.newInstance(MaterialReference.class, MaterialRefList.class);
        Unmarshaller um = jc.createUnmarshaller();
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        Object o = um.unmarshal(is);
        System.out.println(o);


        
    }
}
