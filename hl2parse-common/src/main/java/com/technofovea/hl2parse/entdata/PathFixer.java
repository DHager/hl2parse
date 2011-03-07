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
package com.technofovea.hl2parse.entdata;

/**
 * A PathFixer is responsible for taking strings (and associated
 * {@link ValueSource} objects) and returning a "proper" path. For example, a
 * material may be found as "folder/file", lacking the implicit parts that would
 * turn it into "materials/folder/file.vmt".
 *
 * @author Darien Hager
 */
public interface PathFixer {

    /**
     * Given a string and details about where it was found, attempt to convert
     * it into a relative file path. 
     * 
     * If the value does not correspond to a file path (such as symbolic sound
     * names like Ambient.Hum) then a string is returned beginning with the
     * non-path prefix.
     * 
     * @param origPath The original path or string
     * @param src The source where it was found
     * @return A relative path or
     */
    public String fixPath(String origPath, ValueSource src);

    /**
     * Returns a string used by {@link #fixPath(java.lang.String, com.technofovea.hl2parse.entdata.ValueSource)}
     * to prefix non-path values. Typically this should be a character which is
     * not part of any typical valid path, such as a colon.
     * @return The string used to prefix non-path values
     */
    public String getNonPathPrefix();
}
