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
 * Represents a possible value for an FgdProperty of type "choices"
 * @author Darien Hager
 */
public class ChoicesValue implements Comparable<ChoicesValue>{

    private String value;
    private String description;

    public ChoicesValue(String value, String description) {
        this.value= value;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

   

    public String toText() {
        String valueOutput = FgdSpec.quoteVal(value);
        String ret =  valueOutput + ":\""+getDescription()+"\"";
        return ret;
    }

    public int compareTo(ChoicesValue o) {
       int difference =  this.getValue().compareTo(o.getValue());
       if(difference != 0){
           return difference;
       }
       return this.getDescription().compareTo(o.getDescription());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChoicesValue other = (ChoicesValue) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 23 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }



    






}
