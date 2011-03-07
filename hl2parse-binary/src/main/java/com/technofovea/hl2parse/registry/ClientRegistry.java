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
package com.technofovea.hl2parse.registry;

import com.technofovea.hl2parse.JxPathUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class ClientRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ClientRegistry.class);
    protected static final String CDR_SECTION_NAME = "ContentDescriptionRecord";
    BlobFolder root;
    CdrParser cdr = null;
    JXPathContext ctx;

    public ClientRegistry(BlobFolder root) throws BlobParseFailure {
        this.root = root;
        ctx = JXPathContext.newContext(root);
        JxPathUtil.addFunctions(ctx);
        cdr = createCdr();
    }

    CdrParser createCdr() throws BlobParseFailure {
        logger.debug("Finding compressed " + CDR_SECTION_NAME + " section");
        BlobRaw br = (BlobRaw) ctx.getValue("/values[@name='" + CDR_SECTION_NAME + "']");
        if (br == null) {
            throw new BlobParseFailure("Could not find " + CDR_SECTION_NAME + " segment. Check that Steam is up-to-date.");
        }

        logger.debug("Extracting compressed CDR section");
        ByteBuffer expandedBuf = RegParser.decompress(br.asBuffer());

        logger.debug("Creating reader for decompressed CDR data");
        CdrParser cp = new CdrParser(expandedBuf);
        return cp;
    }

    public CdrParser getContentDescriptionRecord() {
        return cdr;
    }

    public JXPathContext getRootContext() {
        return ctx;
    }

    public List<String> getUsernames() {
        logger.debug("Retrieving known Steam username(s)");

        Iterator<String> users = (Iterator<String>) ctx.iterate("/folders[@name='_Users']/folders/*/name");
        List<String> ret = new ArrayList<String>();
        while (users.hasNext()) {
            ret.add(users.next());
        }
        logger.debug("Usernames found: {}", ret);
        return ret;
    }
}
