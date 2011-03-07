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
import java.util.Iterator;
import java.util.List;
import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class GameInfoReader {

    public static final String PLACEHOLDER_ALLSOURCE = "all_source_engine_paths"; // Relative to main appid OR additional ones
    public static final String PLACEHOLDER_GAMEINFODIR = "gameinfo_path"; // Relative to gameinfo file's parent directory
    public static final String PATH_DELIM = "|"; // Marks beginning and end of placeholders
    public static final String DEFAULT_FILENAME = "gameinfo.txt";
    
    private static final Logger logger = LoggerFactory.getLogger(GameInfoReader.class);
    VdfRoot root;
    JXPathContext context;
    File sourceFile;

    public GameInfoReader(VdfRoot rootNode, File sourceFile) {
        this.sourceFile = sourceFile;
        root = rootNode;
        context = JXPathContext.newContext(root);
        JxPathUtil.addFunctions(context);

    }

    /**
     * Retrieves the path to the souce file. This is relevant since some paths
     * within the data are relative to the location of the file itself.
     * @return The File which was parsed. Potentially null, depending on usage.
     */
    public File getSourceFile() {
        return sourceFile;
    }

    public String getGameName() {
        return (String) context.getValue("children[custom:equals(name,'GameInfo')]/attributes[custom:equals(name,'game')]/value");
    }

    public int getSteamAppId() {
        String id = (String) context.getValue("children[custom:equals(name,'GameInfo')]/children[custom:equals(name,'FileSystem')]/attributes[custom:equals(name,'SteamAppId')]/value");
        return Integer.parseInt(id);
    }

    public List<String> getSearchPaths() {
        List<String> ret = new ArrayList<String>();
        Iterator<String> iter = (Iterator<String>) context.iterate("children[custom:equals(name,'GameInfo')]/children[custom:equals(name,'FileSystem')]/children[custom:equals(name,'SearchPaths')]/attributes[custom:equals(name,'game')]/value");
        while (iter.hasNext()) {
            ret.add(iter.next());
        }
        return ret;
    }
    /*
    static List<String> dereferenceSearchPaths(List<String> paths, File gameInfoDirectory, File appIdDirectory ){

    }
     */

    public List<Integer> getAdditionalIds() {
        List<Integer> ret = new ArrayList<Integer>();
        Iterator<String> iter = (Iterator<String>) context.iterate("children[custom:equals(name,'GameInfo')]/children[custom:equals(name,'FileSystem')]/attributes[custom:equals(name,'AdditionalContentId')]/value");
        while (iter.hasNext()) {
            ret.add(Integer.parseInt(iter.next()));
        }
        return ret;
    }
}
