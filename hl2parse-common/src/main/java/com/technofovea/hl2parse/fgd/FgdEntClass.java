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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an anonymous entity-class entry within an FGD file. It
 * functions to group together various properties and input/output values, and may
 * have "modifiers" which affect things such as multiple-inheritance.
 *
 * New objects of this class may also be used in some cases to aggregate properties  and
 * input/output details from the FGD multiple inheritance model. 
 * @author Darien Hager
 */
public class FgdEntClass {

    public static final String INHERITANCE_MODIFIER = "base";
    private static final Logger logger = LoggerFactory.getLogger(FgdEntClass.class);
    String type;
    String description = "";
    Map<String, List<String>> modifiers = new HashMap<String, List<String>>();
    Map<String, FgdProperty> props = new HashMap<String, FgdProperty>();
    Map<String, FgdInput> inputs = new HashMap<String, FgdInput>();
    Map<String, FgdOutput> outputs = new HashMap<String, FgdOutput>();

    static String quoteModifierVal(String type, String val) {
        if ("studio".equalsIgnoreCase(type)) {
            return "\"" + type + "\"";
        } else if ("studioprop".equalsIgnoreCase(type)) {
            return "\"" + type + "\"";
        } else if ("sprite".equalsIgnoreCase(type)) {
            return "\"" + type + "\"";
        } else if ("lightprop".equalsIgnoreCase(type)) {
            return "\"" + type + "\"";
        } else {
            return val;
        }
    }

    public String toText(String name) {

        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(type);
        sb.append(" ");
        for (String modName : modifiers.keySet()) {
            List<String> values = modifiers.get(modName);
            sb.append(modName);
            sb.append("(");
            for (int i = 0; i < values.size(); i++) {

                sb.append(quoteModifierVal(modName, values.get(i)));
                if ((i + 1) != values.size()) {
                    sb.append(",");
                }
            }
            sb.append(")");
        }
        sb.append(" = ");
        sb.append(name);

        if (!"".equals(description)) {
            sb.append(" :\n");
            sb.append("\"");
            sb.append(description);
            sb.append("\"");
        }

        sb.append(" [\n");

        for (String pName : props.keySet()) {
            FgdProperty prop = props.get(pName);
            sb.append(prop.toText(pName));
            sb.append("\n");
        }

        for (String iName : inputs.keySet()) {
            FgdInput input = inputs.get(iName);
            sb.append(input.toText(iName));
            sb.append("\n");
        }

        for (String oName : outputs.keySet()) {
            FgdOutput output = outputs.get(oName);
            sb.append(output.toText(oName));
            sb.append("\n");
        }
        sb.append("]\n");

        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        type = FgdSpec.cleanQuotes(type);
        if(type.startsWith("@")){
            type = type.substring(1);
        }
        this.type = type;
    }

    void addModifier(String name, List<String> values) {
        if (values == null) {
            values = new ArrayList();
        }
        List<String> cleaned = new ArrayList<String>();
        for (String item : values) {
            cleaned.add(FgdSpec.cleanQuotes(item));
        }
        modifiers.put(name.toLowerCase(), cleaned);
    }

    void setDescription(String str) {
        description = FgdSpec.cleanQuotes(str);
    }

    public String getDescription() {
        return description;
    }

    public void addProp(String name, FgdProperty prop) {
        props.put(name, prop);
    }

    public void addInput(String name, FgdInput input) {
        inputs.put(name, input);

    }

    public void addOutput(String name, FgdOutput output) {
        outputs.put(name, output);
    }

    public Map<String, FgdInput> getInputs() {
        return inputs;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    public Map<String, FgdOutput> getOutputs() {
        return outputs;
    }

    public Map<String, FgdProperty> getProps() {
        return props;
    }

    List<FgdEntClass> getDirectParents(FgdSpec spec) {
        if (!modifiers.containsKey(INHERITANCE_MODIFIER)) {
            return new ArrayList<FgdEntClass>();
        }
        List<String> parentNames = modifiers.get(INHERITANCE_MODIFIER);
        List<FgdEntClass> parentObjects = new ArrayList<FgdEntClass>();
        for (String n : parentNames) {
            FgdEntClass p = spec.getEntClass(n);
            if (p != null) {
                parentObjects.add(p);
            } else {
                logger.error("Specified parent {} is missing from specification.", n);
            }
        }
        return parentObjects;
    }

    /**
     * Returns a list of objects to check for inherited state, if nothing is
     * found defined on the current object.
     * @param spec
     * @return
     */
    List<FgdEntClass> getAncestry(FgdSpec spec) {
        /* The ancestry of a class is essentially a tree due to multiple
         * inheritance. However, we can collapse it down into a list based on
         * the order in which they override each other.
         */
        List<FgdEntClass> ancestry = new ArrayList<FgdEntClass>();
        ancestry.add(this);
        appendAncestry(ancestry, spec);
        return ancestry;

    }

    protected void appendAncestry(List<FgdEntClass> ancestry, FgdSpec spec) {
        List<FgdEntClass> parents = getDirectParents(spec);
        for (int i = parents.size() - 1; i >= 0; i--) {
            FgdEntClass p = parents.get(i);
            if (ancestry.contains(p)) {
                // Don't add dupes.
                logger.trace("Ancestry already contains element {}, skipping.", p);
                return;
            }
            ancestry.add(p);
            p.appendAncestry(ancestry, spec);
        }
    }


    public FgdEntClass getInherited(FgdSpec spec) {
        List<FgdEntClass> ancestry = getAncestry(spec);
        // This new object is like our current object, except that it
        // contains combined references to all the multiple-inheritance
        // properties, inputs, outputs, and modifiers.
        FgdEntClass inherited = new FgdEntClass();
        inherited.setType(getType());
        inherited.setDescription(getDescription());
       

        // Due to how putAll layers things in, overwriting what is already there,
        // we must traverse the ancestry list in reverse.
        Collections.reverse(ancestry);
        for (FgdEntClass ancestor : ancestry) {
            inherited.props.putAll(ancestor.props);
            inherited.modifiers.putAll(ancestor.modifiers);
            inherited.outputs.putAll(ancestor.outputs);
            inherited.inputs.putAll(ancestor.inputs);

        }
        // Lastly, remove the base() modifier so that if we re-output things
        // are less confusing.
        inherited.modifiers.remove(INHERITANCE_MODIFIER);
        return inherited;
    }
}
