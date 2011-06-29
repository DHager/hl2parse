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

import com.technofovea.hl2parse.JxPathUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is intended to be a convenient way to parse and group SDK game information.
 * Its values are set at creation time and do not change to reflect the underlying
 * document.
 * @author Darien Hager
 */
public class GameConfigReader {

    /**
     * This class is intended to be a convenient way to group SDK game information.
     * Its values are set at creation time and do not change to reflect the underlying
     * document.
     */
    public static class Game {

        String name;
        List<File> fgds = new ArrayList<File>();
        File gameDir;
        int mapFormat;
        File vmfDir;
        File bspDir;

        Game(JXPathContext relativeContext) {
            relativeContext.getVariables().declareVariable("gamedir", KEY_GAMEDIR);
            relativeContext.getVariables().declareVariable("hammeropts", NODE_HAMMER);
            relativeContext.getVariables().declareVariable("mapformat", KEY_MAPFORMAT);
            relativeContext.getVariables().declareVariable("vmfdir", KEY_MAPSRC);
            relativeContext.getVariables().declareVariable("bspdir", KEY_MAPDEST);
            relativeContext.getVariables().declareVariable("fgdprefix", PREFIX_FGDS);

            name = (String) relativeContext.getValue("name");
            gameDir = new File((String) relativeContext.getValue("attributes[custom:equals(name,$gamedir)]/value"));
            mapFormat = Integer.parseInt((String) relativeContext.getValue("children[custom:equals(name,$hammeropts)]/attributes[custom:equals(name,$mapformat)]/value"));
            vmfDir = new File((String) relativeContext.getValue("children[custom:equals(name,$hammeropts)]/attributes[custom:equals(name,$vmfdir)]/value"));
            bspDir = new File((String) relativeContext.getValue("children[custom:equals(name,$hammeropts)]/attributes[custom:equals(name,$bspdir)]/value"));

            // FGDs are tougher, since they may end in GameDataN where N is 0,1,2,etc.
            Iterator<VdfAttribute> attrs = (Iterator<VdfAttribute>) relativeContext.iterate("children[custom:equals(name,$hammeropts)]/attributes[custom:startswith(name,$fgdprefix)]");
            SortedMap<Integer, String> fgdOrder = new TreeMap<Integer, String>();
            while (attrs.hasNext()) {
                VdfAttribute a = attrs.next();
                assert (a.getName().toLowerCase().startsWith(PREFIX_FGDS.toLowerCase()));
                String numericPart = a.getName().substring(PREFIX_FGDS.length());
                Integer intVal;
                try {
                    intVal = new Integer(numericPart);
                    fgdOrder.put(intVal, a.getValue());
                } catch (NumberFormatException e) {
                    //TODO log
                }
            }
            fgds = new ArrayList<File>();
            for (String path : fgdOrder.values()) {
                fgds.add(new File(path));
            }

        }

        public File getBspDir() {
            return bspDir;
        }

        public List<File> getFgds() {
            // Return a copy
            return new ArrayList<File>(fgds);
        }

        public File getGameDir() {
            return gameDir;
        }

        public int getMapFormat() {
            return mapFormat;
        }

        public String getName() {
            return name;
        }

        public File getVmfDir() {
            return vmfDir;
        }
    }
    static final String ROOT_NODE = "Configs";
    static final String GAME_NODE = "Games";
    static final String KEY_GAMEDIR = "GameDir";
    static final String NODE_HAMMER = "hammer";
    static final String KEY_MAPFORMAT = "MapFormat";
    static final String KEY_MAPSRC = "MapDir";
    static final String KEY_MAPDEST = "BspDir";
    static final String PREFIX_FGDS = "GameData";
    static final String KEY_SDKVERSION = "SDKVersion";

    private static final Logger logger = LoggerFactory.getLogger(GameConfigReader.class);
    VdfRoot root;
    JXPathContext context;
    Set<Game> games = new HashSet<Game>();
    int sdkVersion;

    public GameConfigReader(VdfRoot rootNode) {
        root = rootNode;
        context = JXPathContext.newContext(root);
        JxPathUtil.addFunctions(context);

        context.getVariables().declareVariable("rootname", ROOT_NODE);
        context.getVariables().declareVariable("gamegroup", GAME_NODE);
        context.getVariables().declareVariable("sdkver", KEY_SDKVERSION);

        logger.trace("Checking file for defined games...");
        for (Pointer p :  getGamePointers()) {
            JXPathContext relativeContext = context.getRelativeContext(p);
            Game g = new Game(relativeContext);
            logger.trace("Found game: {}",g.getName());
            games.add(g);
        }

        try{
            sdkVersion = new Integer((String) context.getValue("children[custom:equals(name,$rootname)]/attributes[custom:equals(name,$sdkver)]/value"));
        }catch(JXPathException jex){
            logger.warn("Unable to determine SDK version in game-config",jex);
            sdkVersion = -1;
        }

    }

    List<Pointer> getGamePointers() {
        List<Pointer> ret = new ArrayList<Pointer>();
        Iterator<Pointer> iter = (Iterator<Pointer>) context.iteratePointers("children[custom:equals(name,$rootname)]/children[custom:equals(name,$gamegroup)]/children[*]");
        while (iter.hasNext()) {
            ret.add(iter.next());
        }
        return ret;
    }

    public Set<Game> getGames() {
        // Return a copy
        return new HashSet<Game>(games);
    }

    public int getSdkVersion() {
        return sdkVersion;
    }
}
