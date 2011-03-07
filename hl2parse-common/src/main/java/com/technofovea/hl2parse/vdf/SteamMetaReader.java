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

import com.technofovea.hl2parse.*;
import com.technofovea.hl2parse.JxPathUtil;
import com.technofovea.hl2parse.vdf.VdfAttribute;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.jxpath.JXPathContext;

/**
 *
 * @author Darien Hager
 */
public class SteamMetaReader {

    VdfRoot root;
    JXPathContext context;

    

    public SteamMetaReader(VdfRoot rootNode) {
        root = rootNode;
        context = JXPathContext.newContext(root);
        JxPathUtil.addFunctions(context);


    }

    public String getAutoLogon() {
        return (String) context.getValue("children[custom:equals(name,'SteamAppData')]/attributes[custom:equals(name,'AutoLoginUser')]/value");
    }
}
