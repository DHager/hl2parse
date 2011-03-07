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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an anonymous property on an entity-class. Certain accessors on
 * this class only make sense if the object's type is set correctly.
 * @author Darien Hager
 */
public class FgdProperty {
    public static final String TYPE_CHOICES = "choices";
    public static final String TYPE_FLAGS = "flags";

    String type;
    boolean readonly = false;
    String shortDesc = "";
    String longDesc = "";
    String defaultVal = "";
    List<FlagValue> flags = new ArrayList<FlagValue>();
    List<ChoicesValue> options = new ArrayList<ChoicesValue>();



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * Only applicable if the type of this property is set to "flags"
     * @return A modifiable list of flags.
     */
    public List<FlagValue> getFlags() {
        return flags;
    }

    /**
     * Only applicable if the type of this property is set to "choices"
     * @return A modifiable list of options.
     */
    public List<ChoicesValue> getOptions() {
        return options;
    }

    public String toText(String name) {

        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("(");
        sb.append(type);
        sb.append(") ");

        sb.append(": \"");
        sb.append(shortDesc);
        sb.append("\" : ");

        // If default value is integer, do not quote
        String temp = FgdSpec.quoteVal(defaultVal);
        sb.append(temp);

        sb.append(" : \"");
        sb.append(longDesc);
        sb.append("\" ");

        if (TYPE_CHOICES.equalsIgnoreCase(type)) {
            sb.append("=\n[\n");
            for (ChoicesValue o : options) {
                sb.append(o.toText());
                sb.append("\n");
            }
            sb.append("]");
        } else if (TYPE_FLAGS.equalsIgnoreCase(type)) {
            sb.append("=\n[\n");

            for (FlagValue f : flags) {
                sb.append(f.toText());
                sb.append("\n");
            }
            sb.append("]");
        } else {
        }

        return sb.toString();
    }

    void setShortDescription(String str) {
        shortDesc = str;
        if(shortDesc == null){
            shortDesc = "";
        }
    }

    void setLongDescription(String str) {
        longDesc = str;
        if(longDesc == null){
            longDesc = "";
        }
    }

    void setDefault(String str) {
        defaultVal = str;
        if(defaultVal == null){
            defaultVal = "";
        }
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public String getShortDesc() {
        return shortDesc;
    }
}
