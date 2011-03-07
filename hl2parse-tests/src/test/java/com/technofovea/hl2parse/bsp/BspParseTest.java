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
package com.technofovea.hl2parse.bsp;

import com.technofovea.hl2parse.bsp.SourceMapAnalyzer;
import com.technofovea.hl2parse.ParseUtil;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Darien
 */
public class BspParseTest {

    static final String BSP_LOC = "test_bsp_parse.bsp";
    //static final String BSP_LOC = "../sdk_ctf_2fort.bsp";

    @Test
    public void testBspParsing() throws Exception {
        ByteBuffer bb = ParseUtil.mapFile(new File(this.getClass().getResource(BSP_LOC).toURI()));

        SourceMapAnalyzer sma = new SourceMapAnalyzer(bb);

        Set<String> expectedMaterials = new HashSet<String>();
        expectedMaterials.add("materials/BRICK/BRICKFLOOR001A");
        expectedMaterials.add("materials/STONE/STONEWALL006B");
        expectedMaterials.add("materials/STONE/STONEWALL033A");
        expectedMaterials.add("materials/PLASTER/PLASTERCEILING003A");

        // Two decals
        expectedMaterials.add("materials/wood/wooddoor008a");
        expectedMaterials.add("materials/decals/decalgraffiti003a");

        /*//Tool textures
        expectedMaterials.add("materials/TOOLS/TOOLSTRIGGER");
        expectedMaterials.add("materials/TOOLS/TOOLSSKYBOX");
         */

        /*//Cubemaps
        expectedMaterials.add("materials/maps/test_bsp_parse/tile/tilefloor001b_0_0_-896");
        expectedMaterials.add("materials/maps/test_bsp_parse/tile/tilefloor001b_352_352_-896");
        expectedMaterials.add("materials/maps/test_bsp_parse/tile/tilefloor009b_0_0_-896");
        expectedMaterials.add("materials/maps/test_bsp_parse/stone/stonestair001a_0_0_-896");
         */


        Set<String> foundTextures = new HashSet<String>();
        for (String found : sma.getBrushTextures()) {
            foundTextures.add(found.toLowerCase());
        }
        for (String expected : expectedMaterials) {
            expected = expected.toLowerCase();
            Assert.assertTrue(expected, foundTextures.contains(expected));
        }

        Map<String, Set<Integer>> foundStaticProps = sma.getStaticPropSkins();

        Assert.assertTrue(foundStaticProps.get("models/props_trainstation/bench_indoor001a.mdl").contains(0));
        Assert.assertTrue(foundStaticProps.get("models/props_trainstation/column_light001b.mdl").contains(1));
        Assert.assertTrue(foundStaticProps.get("models/props_trainstation/trashcan_indoor001a.mdl").contains(0));
        Assert.assertTrue(foundStaticProps.get("models/props_trainstation/payphone001a.mdl").contains(0));

    }

/*
    @Test
    public void testEntdataParsing() throws Exception {
        ByteBuffer bb = ParseUtil.mapFile(new File(this.getClass().getResource(BSP_LOC).toURI()));

        SourceMapAnalyzer sma = new SourceMapAnalyzer(bb);

        String entData = sma.getEntData();
        VdfParser vfp = new VdfParser(entData);
        VdfNode n = vfp.go();
        MapEntityData med = new MapEntityData(n);

        Set<String> foundDynamicModels = med.getDynamicModels();
        Set<String> expectedDynamicModels = new HashSet<String>();

        expectedDynamicModels.add("models/props_borealis/bluebarrel001.mdl");

        for (String expected : expectedDynamicModels) {
            Assert.assertTrue(expected, foundDynamicModels.contains(expected));
        }
    }
*/
    @Test
    public void testPackedFiles() throws Exception {
        ByteBuffer bb = ParseUtil.mapFile(new File(this.getClass().getResource(BSP_LOC).toURI()));

        SourceMapAnalyzer sma = new SourceMapAnalyzer(bb);

        Set<String> packedFilePaths = new HashSet<String>();
        ZipFile zf = sma.getPackedFiles();
        Enumeration<? extends ZipEntry> en = zf.entries();
        while (en.hasMoreElements()) {
            ZipEntry entry = en.nextElement();
            packedFilePaths.add(entry.getName());
            //System.out.println(entry.getName());
        }

        Assert.assertTrue(packedFilePaths.contains("materials/maps/test_bsp_parse/cubemapdefault.vtf"));

        if (false) {
            File f = sma.getPackedFile("materials/maps/test_bsp_parse/tile/tilefloor001b_352_352_-896.vmt");
            FileReader fr = new FileReader(f);
            LineNumberReader lnr = new LineNumberReader(fr);
            String line = "";
            while(line != null){
                line = lnr.readLine();
                System.out.println(line);
            }
            sma.getPackedFile("materials/maps/test_bsp_parse/tile/tilefloor001b_352_352_-896.vmt");
            sma.getPackedFile("materials/maps/test_bsp_parse/tile/tilefloor001b_352_352_-896.vmt");

        }
    }
}
