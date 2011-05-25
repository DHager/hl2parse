/*
 * 
 */
package com.technofovea.hl2parse.xml;

import java.util.Collection;

/**
 *
 * @author Darien Hager
 */
public interface MaterialReference {

    public Collection<String> getIgnoreValues();

    public Collection<String> getNames();

    public ReferenceType getType();

    public boolean hasName(String name);

    public boolean hasIgnoreValue(String val);
}
