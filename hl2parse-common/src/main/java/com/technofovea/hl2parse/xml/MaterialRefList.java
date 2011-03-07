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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Darien Hager
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class MaterialRefList implements Iterable<MaterialReference>{

    @XmlElement(required=true,name="item")
    protected List<MaterialReference> items = new ArrayList<MaterialReference>();

    public List<MaterialReference> getItems() {
        return items;
    }

    public void setItems(List<MaterialReference> items) {
        this.items = items;
    }

    public int size() {
        return items.size();
    }

    public MaterialReference remove(int index) {
        return items.remove(index);
    }

    public Iterator<MaterialReference> iterator() {
        return items.iterator();
    }

    public MaterialReference get(int index) {
        return items.get(index);
    }

    public boolean contains(MaterialReference o) {
        return items.contains(o);
    }

    public void clear() {
        items.clear();
    }

    public boolean add(MaterialReference e) {
        return items.add(e);
    }

    public String toString() {
        return items.toString();
    }

    public ListIterator<MaterialReference> listIterator(int index) {
        return items.listIterator(index);
    }

    public ListIterator<MaterialReference> listIterator() {
        return items.listIterator();
    }


    
    

    
}
