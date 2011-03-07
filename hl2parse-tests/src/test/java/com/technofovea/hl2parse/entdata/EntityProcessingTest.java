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
package com.technofovea.hl2parse.entdata;

import com.technofovea.hl2parse.fgd.DefaultLoader;
import com.technofovea.hl2parse.fgd.FgdEntClass;
import com.technofovea.hl2parse.fgd.FgdSpec;
import com.technofovea.hl2parse.vdf.SloppyParser;
import com.technofovea.hl2parse.vdf.ValveTokenLexer;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Darien
 */
public class EntityProcessingTest {

    private boolean checkContains(Collection<String> c, String target) {
        boolean found = false;
        for (String s : c) {
            if (target.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void temp() throws Exception {

        final String fgdPath = "tf.fgd";
        final String entPath = "ctf_2fort.ent";


        File srcFile = new File(getClass().getResource(fgdPath).toURI());
        InputStream entStream = getClass().getResourceAsStream(entPath);
        ValveTokenLexer vfp = new ValveTokenLexer(new ANTLRInputStream(entStream));
        SloppyParser sp = new SloppyParser(new CommonTokenStream(vfp));
        VdfRoot entRoot = sp.main();
        List<MapEntity> ents = MapEntity.fromVdf(entRoot);

        FgdSpec spec = new FgdSpec();
        DefaultLoader.fillSpec(srcFile,spec);

        System.out.println(spec.toText());

        final String entName = "func_respawnroom";
        FgdEntClass spawnroom = spec.getEntClass(entName); // Get normal version
        Assert.assertNotNull(spawnroom);
        System.out.println(spawnroom.toText(entName)); // Print sparse form
        FgdEntClass combined = spawnroom.getInherited(spec); // Do inheritance
        System.out.println(combined.toText(entName)); // Print combined form


        DependencyFinder df = new DependencyFinder(spec, ents, DefaultPathFixer.getInstance());


        Set<String> sounds = new HashSet<String>();
        for (ValueSource ep : df.getPropertiesByType(DependencyFinder.PROPTYPE_SOUND)) {
            sounds.addAll(df.getValues(ep));
        }
        Assert.assertTrue(sounds.contains(":Ambient.MachineHum"));

        // Note that this test does NOT include prop_static entities, because
        // those are packaged differently into the BSP file.
        Set<String> models = new HashSet<String>();
        for (ValueSource ep : df.getPropertiesByType(DependencyFinder.PROPTYPE_MODEL)) {
            models.addAll(df.getValues(ep));
        }
        Assert.assertTrue(models.contains("models/props_gameplay/resupply_locker.mdl"));
        Assert.assertTrue(models.contains("models/props_skybox/sunnoon.mdl"));

        Set<String> decals = new HashSet<String>();
        for (ValueSource ep : df.getPropertiesByType(DependencyFinder.PROPTYPE_DECAL)) {
            decals.addAll(df.getValues(ep));
        }
        //TODO use some decals in our test file!

        Set<String> sprites = new HashSet<String>();
        for (ValueSource ep : df.getPropertiesByType(DependencyFinder.PROPTYPE_SPRITE)) {
            sprites.addAll(df.getValues(ep));
        }
        Assert.assertTrue(sprites.contains("materials/Sprites/light_glow03.vmt"));
    }
}
