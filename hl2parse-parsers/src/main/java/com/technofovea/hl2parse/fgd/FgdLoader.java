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

import java.io.IOException;
import org.antlr.runtime.CharStream;

/**
 * Since FGDs can import one-another from relative paths, a loader is responsible
 * for maintaining the correct context, resolving those paths, loading the data,
 * and guarding against cyclic dependencies.
 * 
 * @author Darien Hager
 */
public interface FgdLoader {

    public void reset();

    public CharStream getStream(String path) throws IOException;

}
