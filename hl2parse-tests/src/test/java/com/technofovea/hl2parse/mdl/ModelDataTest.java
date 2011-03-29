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
package com.technofovea.hl2parse.mdl;

import com.technofovea.hl2parse.ParseUtil;
import com.technofovea.hl2parse.vdf.PropDataReader;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author darien.hager
 */
public class ModelDataTest {

    static final String BUOY_MODEL = "buoy_ref.mdl";
    static final String PHY_MODEL = "buoy_ref.phy";    


    protected static VdfRoot parseVdf(String data) throws RecognitionException {
        CharStream cs = new ANTLRStringStream(data);
        ValveTokenLexer lexer = new ValveTokenLexer(cs);
        SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
        VdfRoot root = parser.main();
        return root;
    }

    @Test
    public void testBuoyModel() throws Exception {

        final int skinCount = 3;

        ByteBuffer bb = ParseUtil.mapFile(new File(this.getClass().getResource(BUOY_MODEL).toURI()));
        ModelData md = new ModelData(bb);

        Assert.assertEquals("props_swamp/buoy_ref.mdl", md.getModelName());

        Assert.assertEquals(skinCount, md.getSkinCount());

        List<String> expectedPaths = new ArrayList<String>();
        expectedPaths.add("materials/models/props_swamp/");
        Assert.assertEquals(expectedPaths, md.getTextureSearchPaths());

        /*
         * A quick explanation: MDLs store "skins" as a sort of replacement table.
         * Just because a texture appears in the list from getTexturesForSkin()
         * doesn't necessarily mean that it will be used, only that it would
         * replace whatever was in that same Nth position.
         */
        Map<Integer, List<String>> expectedSkins = new HashMap<Integer, List<String>>();
        expectedSkins.put(0, Arrays.asList(new String[]{"buoy_diffuse.vmt", "buoy_diffuse2.vmt","buoy_diffuse3.vmt"}));
        expectedSkins.put(1, Arrays.asList(new String[]{"buoy_diffuse2.vmt","buoy_diffuse2.vmt","buoy_diffuse3.vmt"}));
        expectedSkins.put(2, Arrays.asList(new String[]{"buoy_diffuse3.vmt","buoy_diffuse2.vmt","buoy_diffuse3.vmt"}));

        Map<Integer,List<String>> actualSkins = new HashMap<Integer, List<String>>();
        for(int i = 0; i < skinCount; i++){
            actualSkins.put(i, md.getTexturesForSkin(i));
        }
        Assert.assertEquals(expectedSkins, actualSkins);
        

    }
    
    @Test
    public void testBuoyPhy() throws Exception {
        ByteBuffer bb = ParseUtil.mapFile(new File(this.getClass().getResource(PHY_MODEL).toURI()));

        PhyData pd = new PhyData(bb);

        String rawPropData = pd.getPropData();

        VdfRoot root = parseVdf(rawPropData);
        PropDataReader pdr = new PropDataReader(root);

        //System.out.println(rawPropData);

        /*
         * TODO pick a different freely-distributable model with multiple gibs
         * Until then this isn't a very good test.
         */
        Assert.assertEquals(0, pdr.getAllGibs().size());
    }
}
