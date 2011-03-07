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




import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.RecognitionException;

/**
 * Used by text parsers to indicate that an error has occurred while parsing
 * text data files.
 * @author Darien Hager
 */
public class ParserException extends RecognitionException {

    String header;
    String message;
    RecognitionException cause;
    public ParserException(BaseRecognizer parser, RecognitionException cause) {
        super(cause.input);
        // Parent class doesn't do causes!
        initCause(cause);

        header = parser.getErrorHeader(cause);
        message = parser.getErrorMessage(cause, parser.getTokenNames());
    }

    @Override
    public String getMessage() {
        return header+": "+message;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+": "+getMessage();
    }
/*
    @Override
    public Throwable getCause() {
        return cause;
    }
 */





}
