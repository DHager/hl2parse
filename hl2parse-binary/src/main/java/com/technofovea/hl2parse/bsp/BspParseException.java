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
package com.technofovea.hl2parse.bsp;

/**
 * Thrown to indicate that a problem has occurred while parsing the binary data
 * of a BSP file or subsections within it.
 * 
 * @author Darien Hager
 */
public class BspParseException extends Exception {

    public BspParseException(Throwable cause) {
        super(cause);
    }

    public BspParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BspParseException(String message) {
        super(message);
    }

    public BspParseException() {
    }
}
