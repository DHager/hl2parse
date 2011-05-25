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
package com.technofovea.hl2parse.vdf;

import com.technofovea.hl2parse.vdf.GameConfigReader.Game;
import com.technofovea.hl2parse.xml.MaterialReference;
import com.technofovea.hl2parse.xml.MaterialReferenceImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;
import static com.technofovea.hl2parse.xml.ReferenceType.*;

/**
 *
 * @author Darien Hager
 */
public class ParseTest {

    static final String STEAM_LOGON_FILE = "SteamAppData.vdf";
    static final String GAMEINFO = "tf2_gameinfo.txt";
    static final String MATERIAL_WATER = "water_2fort.vmt";
    static final String MATERIAL_BLEND = "blendcobbletocobblesnow001.vmt";
    static final String PARTICLE_MANIFEST = "particles_manifest.txt";
    static final String SDK_GAME_CONFIG = "GameConfig.txt";
    static final String SOUNDSCAPE = "soundscapes_2fort.txt";

    private void assertSameItems(String[] expectedArray, Collection<String> actualCollection) {
        Set<String> expected = new HashSet<String>(Arrays.asList(expectedArray));
        Set<String> actual = new HashSet<String>(actualCollection);

        Assert.assertEquals(expected, actual);
    }

    protected static VdfRoot doSloppyParse(InputStream iStream) throws RecognitionException, IOException {
        ANTLRInputStream ais = new ANTLRInputStream(iStream);
        ValveTokenLexer lexer = new ValveTokenLexer(ais);
        SloppyParser parser = new SloppyParser(new CommonTokenStream(lexer));
        VdfRoot root = parser.main();
        return root;
    }

    @Test
    public void testTf2GameInfo() throws Exception {

        File gameInfoFile = new File(getClass().getResource(GAMEINFO).toURI());
        VdfRoot root = doSloppyParse(new FileInputStream(gameInfoFile));
        //System.out.println(root);
        GameInfoReader gi = new GameInfoReader(root, gameInfoFile);

        Assert.assertEquals("Team Fortress 2", gi.getGameName());
        Assert.assertEquals(440, gi.getSteamAppId());

        Assert.assertEquals(0, gi.getAdditionalIds().size());

        List<String> expectedPaths = new ArrayList<String>();
        expectedPaths.add("|gameinfo_path|.");
        expectedPaths.add("tf");
        expectedPaths.add("|all_source_engine_paths|hl2");


        Assert.assertEquals(expectedPaths, gi.getSearchPaths());

    }

    @Test
    public void testOrangeboxSdkConfig() throws Exception {
        final String TITLE_EP2 = "Half-Life 2: Episode Two";
        final String TITLE_TF2 = "Team Fortress 2";
        final String TITLE_PORTAL = "Portal";
        VdfRoot root = doSloppyParse(getClass().getResourceAsStream(SDK_GAME_CONFIG));
        //System.out.println(root);
        GameConfigReader gcr = new GameConfigReader(root);

        Assert.assertEquals(3, gcr.getSdkVersion());


        Map<String, Game> gameMap = gcr.getGames();
        Set<String> expectedGameNames = new HashSet<String>();
        expectedGameNames.add(TITLE_EP2);
        expectedGameNames.add(TITLE_TF2);
        expectedGameNames.add(TITLE_PORTAL);

        Assert.assertEquals(expectedGameNames, gameMap.keySet());

        // Check that individual games seem Ok
        final Game portal = gameMap.get(TITLE_PORTAL);
        final String steamBase = "c:/program files/steam/steamapps/STEAMLOGON";
        Assert.assertEquals(new File(steamBase + "/portal/portal"), portal.getGameDir());
        Assert.assertEquals(4, portal.getMapFormat());
        Assert.assertEquals(new File(steamBase + "/sourcesdk_content/portal/mapsrc"), portal.getVmfDir());
        Assert.assertEquals(new File(steamBase + "/portal/portal/maps"), portal.getBspDir());

        List<File> expectedFgds = new ArrayList<File>();
        expectedFgds.add(new File(steamBase + "/sourcesdk/bin/orangebox/bin/portal.fgd"));

        Assert.assertEquals(expectedFgds, portal.getFgds());


    }

    @Test
    public void testWaterVmf() throws Exception {

        VdfRoot root = doSloppyParse(getClass().getResourceAsStream(MATERIAL_WATER));
        //System.out.println(root);
        Set<MaterialReference> matSettings = new HashSet<MaterialReference>(Arrays.asList(
                new MaterialReferenceImpl(MATERIAL, "$fallbackmaterial"),
                new MaterialReferenceImpl(TEXTURE, "$normalmap"),
                new MaterialReferenceImpl(MATERIAL, "$underwateroverlay"),
                new MaterialReferenceImpl(MATERIAL, "$bottommaterial")));

        MaterialReader matReader = new MaterialReader(root, matSettings);

        String[] expectedTextures = new String[]{
            "materials/water/tfwater001_normal.vtf",};

        assertSameItems(expectedTextures, matReader.getTextures());

        String[] expectedMaterials = new String[]{
            "materials/nature/water_dx70.vmt",
            "materials/water/water_2fort_dx80.vmt",
            "materials/water/water_2fort_beneath.vmt",
            "materials/effects/water_warp.vmt"
        };

        assertSameItems(expectedMaterials, matReader.getMaterials());

    }

    @Test
    public void testBlendVmf() throws Exception {
        VdfRoot root = doSloppyParse(getClass().getResourceAsStream(MATERIAL_BLEND));

        Set<MaterialReference> matSettings = new HashSet<MaterialReference>(Arrays.asList(
                new MaterialReferenceImpl(MATERIAL, "$fallbackmaterial"),
                new MaterialReferenceImpl(TEXTURE, "$basetexture"),
                new MaterialReferenceImpl(TEXTURE, "$blendmodulatetexture"),
                new MaterialReferenceImpl(TEXTURE, "$bumpmap")));

        MaterialReader matReader = new MaterialReader(root, matSettings);

        String[] expectedTextures = new String[]{
            "materials/brick/cobblewall001.vtf",
            "materials/brick/cobblewall001_normal.vtf",
            "materials/brick/cobblewall001_snow.vtf"
        };

        assertSameItems(expectedTextures, matReader.getTextures());


        String[] expectedMaterials = new String[]{};

        assertSameItems(expectedMaterials, matReader.getMaterials());

    }

    @Test
    public void testDefaultParticles() throws Exception {
        VdfRoot root = doSloppyParse(getClass().getResourceAsStream(PARTICLE_MANIFEST));
        ParticleManifestReader pm = new ParticleManifestReader(root);

        String[] expectedPcfs = new String[]{
            "particles/error.pcf",
            "particles/rockettrail.pcf",
            "particles/smoke_blackbillow.pcf",
            "particles/teleport_status.pcf",
            "particles/explosion.pcf",
            "particles/player_recent_teleport.pcf",
            "particles/rocketjumptrail.pcf",
            "particles/rocketbackblast.pcf",
            "particles/flamethrower.pcf",
            "particles/burningplayer.pcf",
            "particles/blood_impact.pcf",
            "particles/blood_trail.pcf",
            "particles/muzzle_flash.pcf",
            "particles/teleported_fx.pcf",
            "particles/cig_smoke.pcf",
            "particles/crit.pcf",
            "particles/medicgun_beam.pcf",
            "particles/water.pcf",
            "particles/stickybomb.pcf",
            "particles/buildingdamage.pcf",
            "particles/nailtrails.pcf",
            "particles/speechbubbles.pcf",
            "particles/bullet_tracers.pcf",
            "particles/nemesis.pcf",
            "particles/disguise.pcf",
            "particles/sparks.pcf",
            "particles/flag_particles.pcf",
            "particles/buildingdamage.pcf",
            "particles/shellejection.pcf",
            "particles/medicgun_attrib.pcf",
            "particles/item_fx.pcf",
            "particles/cinefx.pcf",
            "particles/impact_fx.pcf",
            "particles/conc_stars.pcf",
            "particles/class_fx.pcf",
            "particles/dirty_explode.pcf",
            "particles/smoke_blackbillow_hoodoo.pcf"
        };

        assertSameItems(expectedPcfs, pm.getPcfs());

    }

    @Test
    public void testSoundscape() throws Exception {
        VdfRoot root = doSloppyParse(getClass().getResourceAsStream(SOUNDSCAPE));

        //System.out.println(root);
        SoundScapeReader ss = new SoundScapeReader(root);


        String[] expectedWavs = new String[]{
            "sound/ambient/bird1.wav",
            "sound/ambient/bird2.wav",
            "sound/ambient/bird3.wav",
            "sound/ambient/command_center.wav",
            "sound/ambient/computer_tape.wav",
            "sound/ambient/computer_tape2.wav",
            "sound/ambient/computer_working.wav",
            "sound/ambient/cow1.wav",
            "sound/ambient/cow2.wav",
            "sound/ambient/cow3.wav",
            "sound/ambient/engine_idle.wav",
            "sound/ambient/factory_outdoor.wav",
            "sound/ambient/indoors.wav",
            "sound/ambient/machine_hum.wav",
            "sound/ambient/machine_hum2.wav",
            "sound/ambient/outdoors.wav",
            "sound/ambient/outdoors_quiet_birds.wav",
            "sound/ambient/pondlife.wav",
            "sound/ambient/pondwater.wav",
            "sound/ambient/printer.wav",
            "sound/ambient/train_engine_idle.wav",
            "sound/ambient/underground.wav"
        };

        assertSameItems(expectedWavs, ss.getSoundFiles());


        String[] expectedScapes = new String[]{
            "2fort.Indoor",
            "2fort.OutdoorFort",
            "2fort.OutdoorPond",
            "2fort.Underground",
            "2fort.Underground2"
        };

        assertSameItems(expectedScapes, ss.getSoundscapeNames());
    }

    @Test
    public void testLogonRetrieval() throws Exception {
        InputStream is = this.getClass().getResourceAsStream(STEAM_LOGON_FILE);

        VdfRoot root = doSloppyParse(is);
        SteamMetaReader smr = new SteamMetaReader(root);

        Assert.assertEquals("user@example.com", smr.getAutoLogon());
    }
}
