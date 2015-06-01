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
lexer grammar ValveTokenLexer;

@header {
package com.technofovea.hl2parse.vdf;
import com.technofovea.hl2parse.ParserException;

/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/    
}




@members{
		
	public boolean mismatchIsUnwantedToken(IntStream input, int ttype) {
		// Disable error-recovery method
		return false;
	}

	public boolean mismatchIsMissingToken(IntStream input, BitSet follow) {
		// Disable error-recovery method
		return false;
	}
}

AT 	:  '@';
COMMA 	:  ',';
EXCL 	:  '!';
PLUS 	:  '+';
AMPERSAND :  '&';
STAR 	:  '*';
DOLLAR	:  '$';
EQUALS 	:  '=';
COLON	:  ':';
LSQUARE :  '[';
RSQUARE :  ']';
LPAREN	:  '(';
RPAREN	:  ')';
LBRACE	:  '{';
RBRACE	:  '}';
BSLASH	:  '\\';
PERCENT	:  '%';
LESSER	:  '<';
GREATER	:  '>';
PIPE	:  '|';
SLASH	:  '/';
NULL	:  '\u0000';

fragment DIGIT	:	'0'..'9';
fragment ALPHA	:	'a'..'z'|'A'..'Z';
fragment UNDERSCORE	:	'_';
fragment QUOT 	:	 '"';


IDENT  :	(ALPHA|UNDERSCORE)(ALPHA|DIGIT|UNDERSCORE)*
    ;
    
FLOAT	:	DOT DIGIT+
	|	INT DOT DIGIT+
	;

INT :	MINUS? DIGIT+    ;
    

MINUS 	:	 '-';
DOT 	: 	 '.';
    
STRING
    : QUOT (  ~QUOT | '\\"')* QUOT
    {
    setText(getText().substring(1, getText().length()-1));
    }
    ;

fragment CRETURN	:	'\r';
fragment NLINE		: 	'\n';
fragment SPACE	:	' ';
fragment TAB	:	'\t';
	
COMMENT
    :   '//' ~(CRETURN|NLINE)* (CRETURN|NLINE|EOF) {$channel=HIDDEN;}
    ;
    
  

WS  :   (SPACE|TAB|CRETURN|NLINE)+
    ;


/*
Note that @rulecatch does nothing in the lexer, recommended way is to create
a final "bad" rule to do things.
See http://www.mail-archive.com/il-antlr-interest@googlegroups.com/msg02132.html


BADINPUT : .  { System.err.println("The character '" + $text + "' is not a legal character in this language or something."); } ;


//ParserException carrier = new ParserException(this,ex);
//throw carrier;
*/





