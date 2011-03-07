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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.jxpath.JXPathContext;

/**
 *
 * @author Darien Hager
 */
public class SoundScapeReader {

    private static final String KEY_WAV = "wave";

    private VdfNode root;
    private JXPathContext context;
    public SoundScapeReader(VdfRoot rootNode) {
        root = rootNode;
        context = JXPathContext.newContext(root);
        JxPathUtil.addFunctions(context);
        context.getVariables().declareVariable("wavkey",KEY_WAV);
    }
    public List<String> getSoundFiles(){
        Iterator<String> allSnds = context.iterate("//attributes[custom:equals(name,$wavkey)]/value");

        List<String> ret = new ArrayList<String>();
        while (allSnds.hasNext()) {
            String path = allSnds.next();
            ret.add("sound/"+path);
        }
        return ret;
    }
    public List<String> getSoundscapeNames(){
        Iterator scapes = context.iterate("/children/name");
        List<String> ret = new ArrayList<String>();
        while (scapes.hasNext()) {
            ret.add((String) scapes.next());
        }
        return ret;

    }


}
