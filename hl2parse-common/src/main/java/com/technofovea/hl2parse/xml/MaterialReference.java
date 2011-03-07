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
package com.technofovea.hl2parse.xml;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a material property that references another asset. For example,
 * $basetexture references an external texture.
 * @author Darien Hager
 */
@XmlAccessorType(XmlAccessType.NONE)
public class MaterialReference {

    @XmlType
    @XmlEnum(value = String.class)
    public static enum ReferenceType {

        @XmlEnumValue("texture")
        TEXTURE,
                
        @XmlEnumValue("material")
        MATERIAL
    }


    @XmlElement(required = true)
    @XmlList // Allows whitespace-separation
    protected Set<String> names = new HashSet<String>();;
    @XmlElement(required = false,name="ignoreValue")
    protected Set<String> ignoreValues = new HashSet<String>();
    @XmlElement(required = false, defaultValue="texture")
    protected ReferenceType type = ReferenceType.TEXTURE;

    public Set<String> getIgnoreValues() {
        return ignoreValues;
    }

    public void setIgnoreValues(Set<String> ignoreValues) {
        this.ignoreValues = ignoreValues;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = new HashSet<String>();
        for(String n: names){
            this.names.add(n.toLowerCase());
        }
    }

    public ReferenceType getType() {
        return type;
    }

    public void setType(ReferenceType type) {
        this.type = type;
    }

    public void addName(String name){
        names.add(name.toLowerCase());
    }

    public void addIgnoreValue(String val){
        ignoreValues.add(val);
    }

    public boolean hasName(String name){
        return names.contains(name.toLowerCase());
    }

    public boolean hasIgnoreValue(String val){
        return ignoreValues.contains(val);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MaterialReference other = (MaterialReference) obj;
        if (this.names != other.names && (this.names == null || !this.names.equals(other.names))) {
            return false;
        }
        if (this.ignoreValues != other.ignoreValues && (this.ignoreValues == null || !this.ignoreValues.equals(other.ignoreValues))) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.names != null ? this.names.hashCode() : 0);
        hash = 97 * hash + (this.ignoreValues != null ? this.ignoreValues.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

}
