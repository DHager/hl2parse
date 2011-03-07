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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class PropDataReader {

    private static final Logger logger = LoggerFactory.getLogger(PropDataReader.class);
    VdfRoot root;
    JXPathContext context;

    public PropDataReader(VdfRoot rootNode) {
        root = rootNode;
        context = JXPathContext.newContext(root);
        JxPathUtil.addFunctions(context);
    }

    public List<String> getRagdollModels() {
        Iterator<String> allSnds = context.iterate("children[custom:equals(name,'break')]/attributes[custom:equals(name,'ragdoll')]/value");

        List<String> ret = new ArrayList<String>();
        while (allSnds.hasNext()) {
            String path = allSnds.next();
            ret.add("models/" + path + ".mdl");
        }
        return ret;
    }

    public List<String> getRigidGibModels() {
        Iterator<String> allSnds = context.iterate("children[custom:equals(name,'break')]/attributes[custom:equals(name,'model')]/value");

        List<String> ret = new ArrayList<String>();
        while (allSnds.hasNext()) {
            String path = allSnds.next();
            ret.add(path);
        }
        return ret;
    }

    public Set<String> getAllGibs() {
        Set<String> ret = new HashSet<String>();
        ret.addAll(getRagdollModels());
        ret.addAll(getRigidGibModels());
        return ret;

    }
}
