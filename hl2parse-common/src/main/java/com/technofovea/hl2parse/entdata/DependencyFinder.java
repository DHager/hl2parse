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
package com.technofovea.hl2parse.entdata;

import com.technofovea.hl2parse.fgd.FgdEntClass;
import com.technofovea.hl2parse.fgd.FgdProperty;
import com.technofovea.hl2parse.fgd.FgdSpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A DependencyFinder is what brings together the FGD metadata (describing what
 * properties can be set on entities) with entity data for a map.
 *
 * For example, the FGD provides the information about what entity-classes and
 * properties store references to models. The entity-data from the map may
 * specify some of those entity-classes. Together, it is possible to determine
 * all of the models in-use in the map.
 *
 * @todo support color-correction
 * @author Darien Hager
 */
public class DependencyFinder {

    private static final Logger log = LoggerFactory.getLogger(DependencyFinder.class);
    /**
     * The abstract FGD base class which should be skipped
     */
    public static final String BASECLASS = "BaseClass";

    /**
     * The property type string for materials
     */
    public static final String PROPTYPE_MATERIAL = "material";
    /**
     * The property type string for choreographed scenes
     */
    public static final String PROPTYPE_SCENE = "scene";
     /**
     * The property type string for sounds
     */
    public static final String PROPTYPE_SOUND = "sound";
     /**
     * The property type string for sprites (may be either .spr or .vmt)
     */
    public static final String PROPTYPE_SPRITE = "sprite";
     /**
     * The property type string for models
     */
    public static final String PROPTYPE_MODEL = "studio";

    /**
     * The property type for decals
     */
    public static final String PROPTYPE_DECAL = "decal";
    /**
     * A special constant for skyboxes, since details of the "worldspawn" entity
     * are not typically enumerated in an FGD file, and skyboxes are somewhat
     * different than most material references.
     */
    public static final ValueSource TYPE_SKYBOX = new ValueSource("worldspawn","skyname","string");
    /**
     * Color correction file references
     */
    public static final ValueSource TYPE_COLOR_CORRECTION = new ValueSource("color_correction", "filename", "string");
    /**
     * Color correction volume file references
     */
    public static final ValueSource TYPE_COLOR_CORRECTION_VOLUME = new ValueSource("color_correction_volume", "filename", "string");

    FgdSpec fgd;
    List<MapEntity> entities;
    List<ValueSource> allSources = new ArrayList<ValueSource>();
    /**
     * Defaults to an "identity" fixer whose output is the same as its input.
     */
    PathFixer fixer;

    /**
     * Creates a new finder based on the given FGD information, list of entities,
     * and a fixer helper.
     *
     * @param spec The FGD data spec
     * @param entities A list of parsed entities from map data
     * @param fixer A path-fixer so that outputs are better-standardized
     */
    public DependencyFinder(FgdSpec spec, List<MapEntity> entities, PathFixer fixer) {
        this.fgd = spec;
        this.entities = entities;
        this.fixer = fixer;
        createCache();
    }

    void createCache() {
        log.debug("Creating entity property cache structures");
        allSources = new ArrayList<ValueSource>();
        for (String classname : fgd.getEntClassNames()) {
            FgdEntClass entclass = fgd.getEntClass(classname);
            if (entclass.getType().equalsIgnoreCase(BASECLASS)) {
                log.trace("Skipping baseclass {}", entclass.getType());
                continue;
            }
            FgdEntClass combined = entclass.getInherited(fgd);
            Map<String, FgdProperty> props = combined.getProps();
            for (String propname : props.keySet()) {
                FgdProperty prop = props.get(propname);
                ValueSource newVal = new ValueSource(classname, propname, prop.getType());
                allSources.add(newVal);
            }
        }
        allSources.addAll(getSpecialWorldspawnCases());
    }

    List<ValueSource> getSpecialWorldspawnCases() {
        List<ValueSource> ret = new ArrayList<ValueSource>();
            ret.add(new ValueSource("worldspawn", "detailmaterial", PROPTYPE_MATERIAL));

        return ret;
    }

    public PathFixer getFixer() {
        return fixer;
    }

    public void setFixer(PathFixer fixer) {
        this.fixer = fixer;
    }

    /**
     * Retrieves all of the possible {@link ValueSource}s that have the
     * given data-type, regardless of whether they occur in the map entity data.
     *
     * Note that this method will not help in certain special cases where the
     * property type string is insufficient to identify a dependency, for example
     * with color_correction or skyboxes.
     *
     * @param type A datatype. For example, {@link #PROPTYPE_MATERIAL}
     * @return A set of ValueSource objects
     */
    public Set<ValueSource> getPropertiesByType(String type) {
        Set<ValueSource> matching = new HashSet<ValueSource>();
        for (ValueSource candidate : allSources) {
            if (type.equalsIgnoreCase(candidate.getDataType())) {
                matching.add(candidate);
            }
        }
        return matching;
    }

    /**
     * From the map entity data, extract all property values which correspond
     * with the given {@link ValueSource}.
     * @param valSrc The type of class and property to look for
     * @return A list of all the values found for that class/property combination.
     */
    public List<String> getValues(ValueSource valSrc) {
        List<String> ret = new ArrayList<String>();

        // Todo examine optimizations if profiling shows a problem
        for (MapEntity ent : entities) {
            String entClass = ent.getEntityClass();
            String propName = valSrc.getPropertyName();
            if (!entClass.equalsIgnoreCase(valSrc.getClassName())) {
                continue;
            }
            if (!ent.containsKey(propName)) {
                continue;
            }

            // Get the "raw" strings from the entity data
            List<String> rawStrings = ent.getValues(valSrc.getPropertyName());
            
            // Some values have implicit file extensions or subdirectories, 
            // which we want to fix before returning.
            List<String> fixedStrings = new ArrayList<String>(rawStrings.size());
            for(String s: rawStrings){
                String fixed =fixer.fixPath(s,valSrc);
                if(fixed.startsWith(fixer.getNonPathPrefix())){
                    log.debug("Skipping apparent non-path: {}",fixed.substring(fixer.getNonPathPrefix().length()));
                }
                fixedStrings.add(fixed);
            }
            ret.addAll(fixedStrings);

        }
        return ret;
    }

}
