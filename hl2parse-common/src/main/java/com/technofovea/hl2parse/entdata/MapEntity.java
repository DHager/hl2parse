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

import com.technofovea.hl2parse.vdf.VdfAttribute;
import com.technofovea.hl2parse.vdf.VdfNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a single entity found within a map.
 * 
 * @author Darien Hager
 */
public class MapEntity {

    private static final Logger logger = LoggerFactory.getLogger(MapEntity.class);
    static String KEY_CLASS = "classname";
    static String KEY_ID = "hammerid";
    /**
     * The class-name of this entity
     */
    protected String entityClass = null;
    /**
     * The optional Hammer-ID of this entity, or -1 if none found.
     */
    protected int hammerId = -1;
    /**
     * The property keys and values associated with this entity. Note that in
     * several situations it is permissible for multiple key/value pairs using
     * the same key.
     */
    protected Map<String, List<String>> attrs = new HashMap<String, List<String>>();

    /**
     * Creates a new list of entity objects after the map entity data has been
     * parsed into a {@link VdfNode} tree.
     * @param rootNode The root node for the tree.
     * @return A list of map entities, possibly blank.
     * @throws EntdataException If there was a problem interpreting the tree.
     */
    public static List<MapEntity> fromVdf(VdfNode rootNode) throws EntdataException {
        List<MapEntity> entities = new ArrayList<MapEntity>();
        for (VdfNode entNode : rootNode.getChildren()) {
            MapEntity e = new MapEntity(entNode);
            entities.add(e);
        }
        return entities;
    }

    /**
     * Creates a new map entity with the given keys and values. Keys are
     * automatically checked for class-name information.
     *
     * @param attributes All attributes on this entity, including class-name and
     * other "special" values.
     * @throws EntdataException If there was a problem interpreting the data.
     */
    public MapEntity(Map<String, List<String>> attributes) throws EntdataException {
        if (!attributes.containsKey(KEY_CLASS)) {
            throw new EntdataException("No class-name specified for entity");
        }
        if (!(attributes.get(KEY_CLASS).size() < 1)) {
            throw new EntdataException("No class-name specified for entity");
        }

        entityClass = attributes.remove(KEY_CLASS).get(0).toLowerCase().trim();
        for (String key : attributes.keySet()) {
            key = key.trim().toLowerCase();
            List<String> vals = attributes.get(key);
            if (key.equalsIgnoreCase(KEY_ID)) {

                try {
                    hammerId = Integer.parseInt(vals.get(0).toLowerCase());
                } catch (NumberFormatException nfe) {
                    throw new EntdataException("Invalid hammer ID: " + vals);
                }
            } else {
                if(vals.size()>0){
                    this.attrs.put(key, vals);
                }//TODO log?
            }
        }

    }

    /**
     * Creates a new map entity from an isolated {@link VdfNode}
     *
     * @param entityNode The node containing key/value mappings describing the entity.
     * @throws EntdataException If there was a problem interpreting the data.
     */
    public MapEntity(VdfNode entityNode) throws EntdataException {

        if (entityNode.getChildren().size() > 0) {
            throw new EntdataException("Entity nodes may not have child blocks, but " + entityNode.getChildren().size() + " found.");
        }
        for (VdfAttribute attr : entityNode.getAttributes()) {
            String key = attr.getName().toLowerCase().trim();
            String val = attr.getValue();



            if (key.equalsIgnoreCase(KEY_CLASS)) {
                entityClass = val.toLowerCase();
            } else if (key.equalsIgnoreCase(KEY_ID)) {
                try {
                    hammerId = Integer.parseInt(val.toLowerCase());
                } catch (NumberFormatException nfe) {
                    throw new EntdataException("Invalid hammer ID: " + val);
                }
            } else {
                if (!attrs.containsKey(key)) {
                    attrs.put(key, new ArrayList());
                }
                attrs.get(key).add(val);


            }
        }
        if (entityClass == null) {
            throw new EntdataException("No entity class found");
        }
        if (hammerId == -1) {
            // Apparently this isn't the terrible case I thought it was.
        }
    }
    /**
     * Retrieve the class-name of this entity (ex. prop_dynamic)
     * @return The class-name
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * Retrieve the hammer-ID for this entity.
     * @return A hammer-ID or -1 if not found
     */
    public int getHammerId() {
        return hammerId;
    }

    /**
     * Gets a set of all property-names on this entity. Note that this does not
     * include "special" values like the class-name or hammer-id.
     * @return A set of property names.
     */
    public Set<String> getKeys() {
        return new HashSet(attrs.keySet());
    }

    /**
     * Check if the given property-name is defined for this entity.
     * @param propertyName The property-name to check.
     * @return True if found, false otherwise.
     */
    public boolean containsKey(String propertyName) {
        return attrs.containsKey(propertyName);
    }

    /**
     * Retrieves the list of values associated with a particular property name
     * on this entity, or null if none found.
     * @param propertyName The property-name to retrieve data for.
     * @return A list of values set for the given property.
     */
    public List<String> getValues(String propertyName) {
        return new ArrayList(attrs.get(propertyName));
    }
}
