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

/**
 * Represents a possible value for an FgdProperty of type "flags"
 * @author Darien Hager
 */
public class FlagValue implements Comparable<FlagValue>{

    private int intValue;
    private String name;
    private boolean defaultOn;

    public FlagValue(int intValue, String name, boolean defaultOn) {
        this.intValue = intValue;
        this.name = name;
        this.defaultOn = defaultOn;
    }

    public boolean isDefaultOn() {
        return defaultOn;
    }

    public int getIntValue() {
        return intValue;
    }

    public String getName() {
        return name;
    }

    public String toText() {
        String ret =getIntValue() + ":\""+getName()+"\":";
        if(isDefaultOn()){
            ret = ret + "1";
        }else{
            ret = ret + "0";
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FlagValue other = (FlagValue) obj;
        if (this.intValue != other.intValue) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.defaultOn != other.defaultOn) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.intValue;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.defaultOn ? 1 : 0);
        return hash;
    }

    public int compareTo(FlagValue other) {
        int valueDifference = this.getIntValue() - other.getIntValue();
        if(valueDifference != 0){
            return valueDifference;
        }

        // Same numbers? Weird but possible, so then sort by the string.
        return this.getName().compareTo(other.getName());
    }






}
