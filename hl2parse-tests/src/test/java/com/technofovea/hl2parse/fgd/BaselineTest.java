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
package com.technofovea.hl2parse.fgd;

import java.io.File;
import java.net.URL;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Darien Hager
 */
public class BaselineTest {

    @Test
    public void test1() throws Exception {
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.ALL);

        URL fileUrl = getClass().getResource("overall.fgd");
        File srcFile = new File(fileUrl.toURI());
        FgdSpec spec = new FgdSpec();
        DefaultLoader.fillSpec(srcFile,spec);

        System.out.println(spec.toText());
        // This test has no classes by design
        Assert.assertEquals(0,spec.getEntClassNames().size());

    }
}
