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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Darien Hager
 */
public class DefaultLoader implements FgdLoader {

    List<File> history = new ArrayList<File>();
    File workingDir = null;

    public static FgdLoader fillSpec(File target, FgdSpec spec) throws IOException, RecognitionException{
        FgdLoader ldr = new DefaultLoader();
        CharStream stream = ldr.getStream(target.getAbsolutePath());
        ForgeGameDataLexer lexer = new ForgeGameDataLexer(stream);
        ForgeGameDataParser parser = new ForgeGameDataParser(new CommonTokenStream(lexer));
        parser.main(spec, ldr);
        return ldr;
    }


    public DefaultLoader() {
    }

    public CharStream getStream(String path) throws IOException {
        File target;
        if (workingDir == null) {
            target = new File(path);
        } else {
            target = new File(workingDir, path);
        }
        if (history.contains(target)) {
            throw new IOException("Cyclic include detected with target: " + path);
        }

        ANTLRInputStream ais = new ANTLRInputStream(new FileInputStream(target));

        if(workingDir == null){
            workingDir = target.getParentFile();
        }
        history.add(target);
        return ais;
    }

    public void reset() {
        history.clear();
        workingDir = null;
    }
/*
    public void loadFrom(File fgd) throws IOException, RecognitionException {
        loadFrom(fgd, new ArrayList<File>());
    }

    public void loadFrom(File fgd, List<File> history) throws IOException, RecognitionException {
        if (history.contains(fgd)) {
            // Cyclic importing detected. We don't want an infinite loop, so...
            throw new IOException("Cyclic include directives detected.");
        }
        history.add(fgd);
        ANTLRInputStream ais = new ANTLRInputStream(new FileInputStream(fgd));
        ForgeGameDataLexer lexer = new ForgeGameDataLexer(ais);
        ForgeGameDataParser parser = new ForgeGameDataParser(new CommonTokenStream(lexer));
        parser.main(fgd, this, history);
    }
 * 
 */
}
