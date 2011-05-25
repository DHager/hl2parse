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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a material property that references another asset. For example,
 * $basetexture references an external texture.
 * @author Darien Hager
 */
public class MaterialReferenceImpl implements MaterialReference {
    protected Set<String> names = new HashSet<String>();
    protected Set<String> ignoreValues = new HashSet<String>();
    protected ReferenceType type = ReferenceType.TEXTURE;

    public MaterialReferenceImpl() {
    }

    public MaterialReferenceImpl(ReferenceType type, String... names) {
        this.names.addAll(Arrays.asList(names));
        this.type = type;
    }

    @Override
    public Collection<String> getIgnoreValues() {
        return Collections.unmodifiableSet(ignoreValues);
    }

    public void setIgnoreValues(Collection<String> ignoreValues) {
        this.ignoreValues.clear();
        this.ignoreValues.addAll(ignoreValues);
    }

    @Override
    public Collection<String> getNames() {
        return Collections.unmodifiableSet(names);
    }

    public void setNames(Collection<String> names) {
        this.names.clear();
        for (String n : names) {
            this.names.add(n.toLowerCase());
        }
    }

    @Override
    public ReferenceType getType() {
        return type;
    }

    public void setType(ReferenceType type) {
        this.type = type;
    }

    public void addName(String name) {
        names.add(name.toLowerCase());
    }

    public void addIgnoreValue(String val) {
        ignoreValues.add(val);
    }

    public boolean hasName(String name) {
        return names.contains(name.toLowerCase());
    }

    public boolean hasIgnoreValue(String val) {
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
        final MaterialReferenceImpl other = (MaterialReferenceImpl) obj;
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
