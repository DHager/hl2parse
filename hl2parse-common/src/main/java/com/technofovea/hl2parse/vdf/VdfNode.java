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

import java.util.ArrayList;
import java.util.List;

/**
 * Used for parsing Valve's various similarly-formatted files. For convenience, many
 * accessors on this class throw exceptions since calling them implicitly signifies
 * you expect the data to be arranged a certain way.
 * 
 * @author Darien Hager
 */
public class VdfNode {

    private String name = "";
    private List<VdfNode> children = new ArrayList<VdfNode>();
    private List<VdfAttribute> attributes = new ArrayList<VdfAttribute>();

    public VdfNode(){
    }

    //TODO custom equality? Deep equality?



    @Override
    public String toString() {
        return toString(0, false);
    }

    public String toPrettyString() {
        return toString(0, true);
    }

    protected String toString(int indentLevel, boolean pretty) {
        StringBuilder sb = new StringBuilder();

        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            tabs.append("\t");
        }
        if (hasName()) {
            sb.append(tabs);
            sb.append("\"");
            sb.append(getName());
            sb.append("\"");
            sb.append("\n");
        }
        sb.append(tabs);
        sb.append("{\n");

        int maxKeyLen = 0;
        if (pretty) {
            // Pad everything so the key/value pairs are easy to read, so we
            // need a first pass to calculate lengths            
            for (VdfAttribute att : attributes) {
                if (att.getName().length() > maxKeyLen) {
                    maxKeyLen = att.getName().length();
                }
            }
        }
        for (VdfAttribute att : attributes) {

            sb.append(tabs);
            sb.append("\t");
            sb.append("\"");
            sb.append(att.getName());
            sb.append("\"");
            if (pretty) {
                sb.append("    ");
                for (int i = 0; i < maxKeyLen - att.getName().length(); i++) {
                    sb.append(" ");
                }
            } else {
                sb.append("    ");
            }
            sb.append("\"");
            sb.append(att.getValue());
            sb.append("\"");

            sb.append("\n");

        }
        for (VdfNode vn : children) {
            String childString = vn.toString(indentLevel + 1, pretty);
            sb.append(childString);
        }
        sb.append(tabs);
        sb.append("}\n");
        return sb.toString();
    }

    public boolean addChild(VdfNode n) {
        if (n == this) {
            return false;
        }
        return children.add(n);
    }

    public void addAttribute(String key, String value) {        
        VdfAttribute va = new VdfAttribute(key, value);
        attributes.add(va);
        return;
    }

    public void setName(String s) {
        name = s;
    }

    public List<VdfNode> getChildren() {
        return children;
    }

    public boolean hasName() {
        return (name != null);
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public List<VdfAttribute> getAttributes() {
        return attributes;
    }       
}
