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
public class ParticleManifestReader {


    private static final String CACHE_MARK = "!";
    private static final String KEY_NAME = "file";
    private VdfRoot root;
    private JXPathContext context;

    public ParticleManifestReader(VdfRoot rootNode) {
        root = rootNode;
        context = JXPathContext.newContext(root);
        JxPathUtil.addFunctions(context);

        context.getVariables().declareVariable("filekey", KEY_NAME);
    }

    public List<String> getPcfs() {
        Iterator scapes = context.iterate("children[1]/attributes[custom:equals(name,$filekey)]/value");
        List<String> ret = new ArrayList<String>();
        while (scapes.hasNext()) {
            String path = (String) scapes.next();
            if (path.startsWith(CACHE_MARK)) {
                path = path.substring(CACHE_MARK.length());
            }
            ret.add(path);
        }
        return ret;
    }
}
