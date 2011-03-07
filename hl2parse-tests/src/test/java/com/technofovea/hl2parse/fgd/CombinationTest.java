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
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darien
 */
public class CombinationTest {

    @Test
    public void testMultipleInheritance() throws Exception {

        URL fileUrl = getClass().getResource("inherit_test.fgd");
        File srcFile = new File(fileUrl.toURI());
        FgdSpec spec = new FgdSpec();
        DefaultLoader.fillSpec(srcFile,spec);

        //System.out.println(spec.toText());
        FgdEntClass combined = spec.getEntClass("CyberBird").getInherited(spec);

        Assert.assertEquals("PointClass", combined.getType());
        Set<String> expectedProps = new HashSet<String>();
        expectedProps.add("manufacturer");
        expectedProps.add("airating");
        expectedProps.add("engines");
        expectedProps.add("wingcount");
        expectedProps.add("maxheight");
        expectedProps.add("conflictingproperty");
        Assert.assertEquals(expectedProps, combined.getProps().keySet());


        // Test that our reference to a property is directly referring to an object
        // associated with a parent ent-class object.
        final Map<String, FgdProperty> machineProps = Collections.unmodifiableMap(spec.getEntClass("Machine").getProps());

        FgdProperty base = machineProps.get("manufacturer");
        FgdProperty inherited = combined.getProps().get("manufacturer");
        Assert.assertEquals(base, inherited);

        // Test that the inheritance order was correct
        FgdProperty expectedConflicter = machineProps.get("conflictingproperty");
        FgdProperty conflicter = combined.getProps().get("conflictingproperty");
        Assert.assertEquals(expectedConflicter, conflicter);

    }

    @Test
    public void testImporting() throws Exception {

        final String ALPHA = "Alpha";
        final String BETA = "Beta";
        final String DELTA = "Delta";
        final String EPSILON = "Epsilon";
        final String GAMMA = "Gamma";

        URL fileUrl = getClass().getResource("importer.fgd");
        File srcFile = new File(fileUrl.toURI());
        FgdSpec spec = new FgdSpec();
        DefaultLoader.fillSpec(srcFile,spec);



        Set<String> expectedClasses = new HashSet<String>();
        expectedClasses.add(ALPHA);
        expectedClasses.add(BETA);
        expectedClasses.add(GAMMA);
        expectedClasses.add(DELTA);
        expectedClasses.add(EPSILON);

        Assert.assertTrue(spec.getEntClassNames().containsAll(expectedClasses));


        // Test that our order-based erasing happened appropriately
        final Set<String> deltaKeyset = spec.getEntClass(DELTA).getProps().keySet();
        final Set<String> gammaKeyset = spec.getEntClass(EPSILON).getProps().keySet();

        Assert.assertTrue(deltaKeyset.contains("eraser"));
        Assert.assertFalse(deltaKeyset.contains("erased"));
        Assert.assertTrue(gammaKeyset.contains("eraser"));
        Assert.assertFalse(gammaKeyset.contains("erased"));
        
    }

    @Test(timeout = 5000)
    public void testCyclicImports() throws Exception {
        // Just make sure they don't fail horribly. They're certainly invalid.
        URL fileUrl = getClass().getResource("cyclic1.fgd");
        File srcFile = new File(fileUrl.toURI());
        FgdSpec spec = new FgdSpec();
        DefaultLoader.fillSpec(srcFile,spec);

    }
}
