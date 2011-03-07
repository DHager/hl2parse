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
package com.technofovea.hl2parse.vdf;

import com.technofovea.hl2parse.*;
import com.technofovea.hl2parse.xml.MaterialRefList;
import com.technofovea.hl2parse.xml.MaterialReference;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class MaterialReader {

    public static final MaterialRefList getDefaultMaterialSettings() throws JAXBException {
        InputStream is = MaterialReader.class.getResourceAsStream("DefaultMaterials.xml");
        return loadFromXml(is);
    }

    public static final MaterialRefList loadFromXml(InputStream stream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(MaterialReference.class, MaterialRefList.class);
        Unmarshaller um = jc.createUnmarshaller();
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        Object o = um.unmarshal(stream);
        return (MaterialRefList) o;
    }
    private static final Logger logger = LoggerFactory.getLogger(MaterialReader.class);
    static final String PATCH_INCLUDE = "include";
    VdfRoot root;
    JXPathContext context;
    MaterialRefList props;
    Set<String> textures = new HashSet<String>();
    Set<String> materials = new HashSet<String>();

    public MaterialReader(VdfRoot rootNode, MaterialRefList props) {
        root = rootNode;
        context = JXPathContext.newContext(root);
        JxPathUtil.addFunctions(context);


        Iterator<VdfAttribute> allAttribs = context.iterate("//attributes");

        while (allAttribs.hasNext()) {
            VdfAttribute attrPair = allAttribs.next();
            String key = attrPair.getName();
            // Strip trailing "2"s for blend texture variations
            if (key.endsWith("2")) {
                logger.trace("Found 2-suffixed attribute variation: {}", key);
                key = key.substring(0, key.length() - 1);
            }


            if (key.equalsIgnoreCase(PATCH_INCLUDE)) {
                // According to docs, the patch shader's "include" directive
                // required a complete path with extension, ex.
                // materials/foo/bar.vmt
                // So we don't prefix or suffix this one
                logger.trace("Found possible patch-include shader material: {}", key);

                materials.add(attrPair.getValue());
            }

            for (MaterialReference ref : props) {
                if (!ref.hasName(key)) { // Case insensitive
                    continue;
                }
                String val = attrPair.getValue();
                if (ref.hasIgnoreValue(val)) { // Case sensitive
                    continue;
                }

                switch (ref.getType()) {
                    case MATERIAL:
                        logger.trace("Found material: {}", val);
                        if (!val.toLowerCase().endsWith(".vmt")) {
                            val += ".vmt";
                        }
                        materials.add("materials/" + val);
                        break;
                    case TEXTURE:
                        logger.trace("Found texture: {}", val);
                        if (!val.toLowerCase().endsWith(".vtf")) {
                            val += ".vtf";
                        }
                        textures.add("materials/" + val);
                        break;
                }
                break; // Break loop
            }
        }
    }

    public Set<String> getMaterials() {
        return new HashSet<String>(materials);
    }

    public Set<String> getTextures() {
        return new HashSet<String>(textures);
    }
}
