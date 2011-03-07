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
package com.technofovea.hl2parse;

import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.JXPathContext;

/**
 * This utility class provides a few simple methods for dealing with JXPath
 * expressions.
 * 
 * @author Darien Hager
 */
public class JxPathUtil {

    /**
     * Adds custom functions onto a context which correspond to
     * static methods on this class, such as {@link #startswith(java.lang.String, java.lang.String)}
     * @param context The context to alter.
     */
    public static void addFunctions(JXPathContext context) {
        context.setFunctions(new ClassFunctions(JxPathUtil.class, "custom"));
    }

    /**
     * A convenience function, this performs a *case-insensitive* equality test,
     * unlike the normal [a=b] xpath convention.
     * @param a A string
     * @param b A string
     * @return True if the two strings are equal, ignoring case. False otherwise.
     */
    public static boolean equals(String a, String b) {
        if (a == null) {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }

    /**
     * This function performs a case-insensitive starts-with test.
     * @param s The string to examine
     * @param prefix The prefix to try
     * @return True if the first string starts with the second, ignoring case.
     */
    public static boolean startswith(String s, String prefix) {
        if (s == null || prefix == null) {
            return false;
        }
        return s.toLowerCase().startsWith(prefix.toLowerCase());
    }
}
