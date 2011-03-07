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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents an FGD 'document' and associated data. 
 * @author Darien Hager
 */
public class FgdSpec {

    Map<String, FgdEntClass> definedClasses = new HashMap<String, FgdEntClass>();
    Map<String, VisGroup> visGroups = new HashMap<String, VisGroup>();
    boolean boundsSet = false;
    int mapMin = -32768;
    int mapMax = 32768;
    Set<String> excludedMaterials = new HashSet<String>();

    static String cleanQuotes(String input) {
        return input.replace("\"", " ");
    }

    static String quoteVal(String value) {
        try {
            int i = Integer.parseInt(value);
            return value;
        } catch (NumberFormatException nfe) {
            return "\"" + value + "\"";
        }

    }


    public int getMapMax() {
        return mapMax;
    }

    public void setMapBounds(int mapMin, int mapMax) throws IllegalArgumentException {
        if (mapMax < mapMin) {
            throw new IllegalArgumentException("Maximum value is less than minimum value");
        }
        this.mapMin = mapMin;
        this.mapMax = mapMax;
        boundsSet = true;
    }

    public void setMapBounds(String mapMin, String mapMax) throws IllegalArgumentException {
        int low = Integer.parseInt(mapMin);
        int high = Integer.parseInt(mapMax);
        setMapBounds(low, high);
    }

    public int getMapMin() {
        return mapMin;
    }

    public void addEntClass(String name, FgdEntClass item) {
        definedClasses.put(name, item);
    }

    public FgdEntClass getEntClass(String name) {
        return definedClasses.get(name);
    }

    public Set<String> getEntClassNames() {
        return new HashSet<String>(definedClasses.keySet());
    }

    public void clearEntClases() {
        definedClasses.clear();
    }

    public VisGroup getVisGroup(String name) {
        return visGroups.get(name);
    }

    public Set<String> getVisGroupNames() {
        return new HashSet<String>(visGroups.keySet());
    }

    public void addVisGroup(String name, VisGroup grp) {
        visGroups.put(name, grp);
    }

    public Set<String> getExcludedMaterials() {
        return excludedMaterials;
    }

    public void setExcludedMaterials(Set<String> excludedMaterials) {
        this.excludedMaterials = excludedMaterials;
    }

    

    public String toText() {
        StringBuilder sb = new StringBuilder();
        if (boundsSet) {
            sb.append("@mapsize(" + mapMin + "," + mapMax + ")\n");
        }
        if (excludedMaterials.size()>0){
            sb.append("@MaterialExclusion\n[");
            for(String item : excludedMaterials){
                sb.append(quoteVal(item));
                sb.append("\n");
            }
            sb.append("]\n");
        }
        for(String groupName : visGroups.keySet()){
            VisGroup vg = visGroups.get(groupName);
            sb.append("@AutoVisGroup = ");
            sb.append(groupName);
            sb.append("\n[\n");
            for(String subName : vg.getSectionNames()){
                sb.append(quoteVal(subName));
                sb.append("\n[\n");
                for(String item : vg.getSectionItems(subName)){
                    
                    sb.append(quoteVal(item));
                    sb.append("\n");
                }
                sb.append("]\n");
            }
            sb.append("]\n");


        
        }
        for (String clazzName : definedClasses.keySet()) {
            FgdEntClass entClass = definedClasses.get(clazzName);
            sb.append(entClass.toText(clazzName));
        }
        return sb.toString();
    }
}
