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
package com.technofovea.hl2parse.fgd;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Darien Hager
 */
public class VisGroup {

    Map<String,Set<String>> sections = new HashMap<String, Set<String>>();

    public Set<String> getSectionNames(){
        return new HashSet<String>(sections.keySet());
    }

    public void removeSection(String name){
        sections.remove(name);
    }
    public Set<String> getSectionItems(String name){
        return sections.get(name);
    }
    public void addSection(String name, Collection<String> entityNames){
        sections.put(name, new HashSet<String>(entityNames));
    }
}
