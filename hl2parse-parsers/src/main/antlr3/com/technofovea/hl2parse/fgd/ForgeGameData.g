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
grammar ForgeGameData;


@parser::header {
package com.technofovea.hl2parse.fgd;
import com.technofovea.hl2parse.ParserException;
import com.technofovea.hl2parse.fgd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;



}
@lexer::header {
package com.technofovea.hl2parse.fgd;
import com.technofovea.hl2parse.ParserException;
}

@parser::members{
	private static final Logger logger = LoggerFactory.getLogger(ForgeGameDataParser.class);
	static final String CHOICE_TYPE = "choices";
	static final String FLAG_TYPE = "flags";
		
	private static String joinTokenList(List listing, String separator){
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for(Object o: listing){			
    			CommonToken tok = ((CommonToken)o);
    			if(isFirst){
    				isFirst = false;				
    			}else{
    				sb.append(separator);
    			}
    			sb.append(tok.getText());
    		}
		return sb.toString();
		
	}
	private static List<String> convertTokenList(List listing){
		ArrayList<String> ret = new ArrayList<String>(listing.size());
		for(Object o: listing){			
    			CommonToken tok = ((CommonToken)o);
    			ret.add(tok.getText());
    		}
    		return ret;
	}
	private static boolean sectionMatches(Token tok, String name){
		String text = tok.getText();
		if(!text.startsWith("@")){
			return false;
		}
		return text.substring(1).equalsIgnoreCase(name);
	}
}
@lexer::members{
	
}

@parser::rulecatch {
	catch (RecognitionException ex) {
		// Instead of printing out error messages, just exit early with an exception.	
		if(ex instanceof ParserException){
			throw(ex);
		}		
		ParserException carrier = new ParserException(this,ex);
		throw carrier;			
	}
}
@lexer::rulecatch {
	catch (RecognitionException ex) {
		// Instead of printing out error messages, just exit early with an exception.	
		if(ex instanceof ParserException){
			throw(ex);
		}		
		ParserException carrier = new ParserException(this,ex);
		throw carrier;			
	}
}


main[FgdSpec root, FgdLoader loaderObj]
scope{
FgdLoader loader;
}
@init{
	$main::loader = loaderObj;
}
	:	 item[root]*
	;

item[FgdSpec root]
	:	(SECTION)=>	{sectionMatches(input.LT(1),"include")}?
			SECTION includeDirective[root]
	|	(SECTION)=>	{sectionMatches(input.LT(1),"mapsize")}?
			SECTION mapsizeDirective[root]
	|	(SECTION)=>	{sectionMatches(input.LT(1),"MaterialExclusion")}?
			SECTION materialExcludeDirective[root]
	|	(SECTION)=>	{sectionMatches(input.LT(1),"AutoVisGroup")}?
			SECTION autoVisgroupDirective[root]
	|	(SECTION)=> 
			classDef[root]
	;
	
		
		
includeDirective[FgdSpec root]
	:	fname=StringLiteral 
	{
                String path = fname.getText();
		logger.trace("Found include directive");
		try{
                        CharStream stream = $main::loader.getStream(path);
                        ForgeGameDataLexer lexer = new ForgeGameDataLexer(stream);
                        ForgeGameDataParser parser = new ForgeGameDataParser(new CommonTokenStream(lexer));
                        parser.main(root, $main::loader);
		}catch (IOException e){
			logger.error("Could not include from {}, because: {}",path,e.getMessage());
		}
	}
	;	
	
mapsizeDirective[FgdSpec root]
	:	 LPAREN min=DecimalLiteral ',' max=DecimalLiteral RPAREN 
	{
	logger.trace("Found mapsize directive");
	root.setMapBounds(min.getText(),max.getText());
	}
	;
	
materialExcludeDirective[FgdSpec root]
	:	 LBRACK dirs+=StringLiteral* RBRACK
	{
		for(Object o: $dirs){			
    			CommonToken tok = ((CommonToken)o);
    			String path = tok.getText();
    			logger.trace("Material exclude dir: {}",path);
    			root.getExcludedMaterials().add(path);
    		}
	}
	;

autoVisgroupDirective[FgdSpec root]
@init{
	VisGroup grp = new VisGroup();
}
	:	EQUALS label=StringLiteral LBRACK autoVisItem[grp]* RBRACK
	{
		root.addVisGroup($label.getText(),grp);
	}
	;
autoVisItem[VisGroup grp]
	:	label=StringLiteral LBRACK entitynames+=StringLiteral* RBRACK
	{
		logger.trace("Found sub-vis item named {}",$label.getText());
		List<String> names = convertTokenList($entitynames);
		logger.trace("Entity listing {}",names);
		grp.addSection($label.getText(),names);	

	}
	;
	
classDef[FgdSpec root]
@init{
	FgdEntClass item = new FgdEntClass();
}
	:	ct=SECTION modifier[item]* EQUALS cn=name ( SEP classDesc[item])? classBody[item] 
	{
	logger.trace("Found class type {} named {}",$ct.text,$cn.text);
	item.setType($ct.text);
	root.addEntClass($cn.text,item);	
	}
	;
	
classBody[FgdEntClass clazz]
	:	LBRACK classItem[clazz]* RBRACK 
	;
		
name	:	Identifier;
	
modifier[FgdEntClass clazz]
@init{
	List<String> values = new ArrayList<String>();
}
	:	modname=Identifier (LPAREN modifierArg[values]? (',' modifierArg[values])* RPAREN)?
	{
		clazz.addModifier($modname.getText(),values);
	}
	;		

modifierArg[List<String> values]
@after{
	values.add($modifierArg.text);
}
	:	val=(StringLiteral
	|	Identifier
	|	tuple
	|	DecimalLiteral)		
	;		
	
tuple	:	DecimalLiteral DecimalLiteral DecimalLiteral
	;
	
classDesc[FgdEntClass clazz]
	:	descr=multilineString
	{
		clazz.setDescription(descr);
	}
	;
	

multilineString returns [String text]
	:	textitems+=StringLiteral (PLUS textitems+=StringLiteral)* PLUS?
	{	
	text = joinTokenList($textitems,"");
	}
	;
		
classItem[FgdEntClass parent]
	:	(Identifier Identifier)=>	{input.LT(1).getText().equals("input")}? 	classInput[parent]
	| 	(Identifier Identifier)=>	{input.LT(1).getText().equals("output")}?	classOutput[parent]
	|	(Identifier propTypeDecl)=>	classProp[parent]
	;

classProp[FgdEntClass parent]
scope{ String propType; }
@init{
	FgdProperty thisProp = new FgdProperty();
}
	:	 n=Identifier dtype=propTypeDecl accessMode=propertyReadOnly propSettings[thisProp]? propContent[thisProp]? 
	{	
	thisProp.setType(dtype);
	thisProp.setReadonly(accessMode);
	parent.addProp($n.text,thisProp);
	logger.trace("Found property named {} with type {}",$n.text,dtype);
	}
	;
	
propTypeDecl returns [String type]
	:	LPAREN dtype=Identifier RPAREN
	{
		$type = $dtype.getText();
		String oldType = $classProp::propType;
		$classProp::propType = $type;
		logger.trace("Setting current property-type from {} to {}",oldType, $classProp::propType);
	}
	;
	
ioTypeDecl returns [String type]
	:	LPAREN dtype=Identifier RPAREN
	{
		$type = $dtype.getText();
	}
	;	

propertyReadOnly returns [Boolean isReadonly]
	:	'readonly'
	{logger.trace("Property is read-only");
	$isReadonly = true;
	}
	|
	{logger.trace("Property is read-write");
	$isReadonly = false;
	}
	;
propContent[FgdProperty currprop]
	@init{logger.trace("Saved type value for semantic predicate is {} ",$classProp::propType);}
	// These depend on the datatype, so we use gated semantic predicates
	:{FLAG_TYPE.equalsIgnoreCase($classProp::propType)}? 
	{logger.trace("Interpreting property options as flags");}
		EQUALS LBRACK flag[currprop]* RBRACK 

	
	|{CHOICE_TYPE.equalsIgnoreCase($classProp::propType)}?
	{logger.trace("Interpreting property options as choices");}
		EQUALS LBRACK choice[currprop]* RBRACK 
		
	|	EQUALS LBRACK RBRACK
	
	;	
propSettings[FgdProperty currprop]
	:	(SEP shortdesc=multilineString? (SEP defaultval=propDefault? (SEP longdesc=multilineString? )? )? )
	{
		currprop.setShortDescription($shortdesc.text);
		currprop.setDefault($defaultval.text);
		currprop.setLongDescription($longdesc.text);
	}
	;
classInput[FgdEntClass parent]
	:	itemType=Identifier iname=Identifier itype=ioTypeDecl (SEP descr=multilineString?)? {$itemType.text.equalsIgnoreCase("input")}?
	{
		//logger.debug("Input found named {} type {}",iname.getText(),itype);
		FgdInput ip = new FgdInput(itype,descr);
		parent.addInput(iname.getText(),ip);
	}
	;	
classOutput[FgdEntClass parent]
	:	itemType=Identifier oname=Identifier otype=ioTypeDecl (SEP descr=multilineString?)? {$itemType.text.equalsIgnoreCase("output")}?
	{
		//logger.debug("Output found named {} type {}",oname.getText(),otype);	
		FgdOutput op = new FgdOutput(otype,descr);
		parent.addOutput(oname.getText(),op);
		
	}
	;	

propDefault	
	: literal
	;
	

choice[FgdProperty currprop]	
	: itemval=literal SEP itemname=literal
{
	ChoicesValue cv = new ChoicesValue($itemval.text,$itemname.text);
	currprop.getOptions().add(cv);
}			
	;
flag[FgdProperty currprop]	
	: itemval=DecimalLiteral SEP itemname=literal (SEP defaultstate=DecimalLiteral)?		
{	
	boolean defaultEnabled = false;
	if(defaultstate != null){
		defaultEnabled = $defaultstate.getText().trim().equals("1");
	}
	
	int intVal = Integer.parseInt($itemval.getText()); // May throw NumberFormatException, but very unlikely given our rules.
	FlagValue fv = new FlagValue(intVal,$itemname.text,defaultEnabled);
	currprop.getFlags().add(fv);
}	
	;
literal	:	DecimalLiteral | StringLiteral
	;
COMMENT
    :   '//' ~(LT)* (LT|EOF) {$channel=HIDDEN;}
    // Block comments not supported
    //|   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN}
    ;
    
SECTION 
	:	'@' Letter+
	;
	    
DecimalLiteral
	: '-'? Digit+ ('.' Digit+)?
	;
	
	
LBRACK 	:	 '[';
RBRACK 	:	 ']';	
LPAREN	:	'('	;
RPAREN	:	')'	;
EQUALS	:	'='	;
SEP	:	':'	;
PLUS	:	'+';	

fragment QUOT	:	'"';
fragment Letter
	:	'a'..'z' | 'A'..'Z'
	;
fragment Digit
	:	'0'..'9'
	;	
fragment Flag
	:	'0'|'1'
	;
fragment Underscore 
	:	'_'
	;

	

	
	
Identifier
	: (Letter|Underscore) (Letter|Underscore|Digit)*
	;


StringLiteral
// Escape sequences are not supported in FGD text.s
	: QUOT ~(QUOT|LT)* QUOT
	{
	setText(getText().substring(1, getText().length()-1));
	}
	;

	
WhiteSpace // Tab, vertical tab, form feed, space, non-breaking space and any other unicode "space separator".
	: ('\t' | '\v' | '\f' | ' ' | '\u00A0' | LT)	{$channel=HIDDEN;}
	;
fragment LT
	: ('\n' | '\r' | '\u2029' | '\u2028')
	;		