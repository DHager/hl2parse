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

/**
 *
 * @author Darien Hager
 */
public class VdfRoot extends VdfNode{

    @Override
    public String toString(int indentLevel, boolean pretty) {
        StringBuilder sb = new StringBuilder();

        for(VdfNode vn : getChildren()){
            sb.append(vn.toString(indentLevel,pretty));
        }
        return sb.toString();
    }

    @Override
    public boolean hasName() {
        return false;
    }

    @Override
    public String getName() {
        return "";
    }
}
