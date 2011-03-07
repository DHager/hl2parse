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
parser grammar SloppyParser;
options{
	tokenVocab=ValveTokenLexer;
}


@header {
package com.technofovea.hl2parse.vdf;
import com.technofovea.hl2parse.ParserException;

/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
}
@rulecatch {
	catch (RecognitionException ex) {
		// Instead of printing out error messages, just exit early with an exception.	
		if(ex instanceof ParserException){
			throw(ex);
		}		
		ParserException carrier = new ParserException(this,ex);
		throw carrier;			
	}
}

main returns [VdfRoot root]
@init{
root = new VdfRoot();
}
	:	WS* block[root] (WS* block[root])* WS* 
        ;

item[VdfNode parent]
    :   block[parent]
    |   scalar[parent]
    ;

block[VdfNode parent]
@init{
	VdfNode current = new VdfNode();
}
    :	(bn=blockName WS?)? LBRACE WS* (item[current] (WS+ item[current])* WS*)? RBRACE
    {
	current.setName($bn.text);
	parent.addChild(current);
    }
    ;
	
blockName
    :   (STRING)=> STRING
    |   unquotedMess
    ;  
scalar[VdfNode parent]
    :	(STRING STRING)=> nt=STRING vt=STRING
    {
    parent.addAttribute($nt.text,$vt.text);
    }
    |	n=scalarName WS+ v=scalarVal
    {
    parent.addAttribute($n.text,$v.text);
    }
    ;    
scalarName
    :   (STRING)=> STRING
    |   unquotedMess
    ;
scalarVal
    :   (STRING)=> STRING
    |   unquotedMess
    ;
    
unquotedMess
	:	(~(STRING|QUOT|COLON|EQUALS|LSQUARE|RSQUARE|LPAREN|RPAREN|LBRACE|RBRACE|WS|COMMENT))+
	;


