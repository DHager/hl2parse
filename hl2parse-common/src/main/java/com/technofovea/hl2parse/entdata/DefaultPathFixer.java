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

/**
 * A default implementation of {@link PathFixer}, this singleton attempts to
 * correct material, sound, and sprite paths. In some cases the sprite/material
 * distinction is ambiguous, and no extension is added.
 *
 * @author Darien Hager
 */
public class DefaultPathFixer implements PathFixer {

    private static DefaultPathFixer instance;

    String nonPathPrefix = ":";

    private DefaultPathFixer() {
    }

    /**
     * Get the singleton instance of this fixer
     * @return The singleton
     */
    public static DefaultPathFixer getInstance() {
        synchronized (DefaultPathFixer.class) {
            if (instance == null) {
                instance = new DefaultPathFixer();
            }
        }
        return instance;

    }

    public String fixPath(String origPath,ValueSource src) {
        if(DependencyFinder.PROPTYPE_MATERIAL.equalsIgnoreCase(src.getDataType())){
            return fixMaterial(origPath,src);
        }else if(DependencyFinder.PROPTYPE_SOUND.equalsIgnoreCase(src.getDataType())){
            return fixSound(origPath,src);
        }else if(DependencyFinder.PROPTYPE_SPRITE.equalsIgnoreCase(src.getDataType())){
            return fixMaterial(origPath, src);
        }else if(DependencyFinder.PROPTYPE_DECAL.equalsIgnoreCase(src.getDataType())){
            return fixMaterial(origPath,src);
        }else if("vtf".equalsIgnoreCase(src.getDataType())){
            return fixTexture(origPath,src);
        }
        return origPath;
    }


    private String fixMaterial(String origPath, ValueSource src) {
        String path = origPath;
        path = path.replace("\\", "/");
        if (!path.toLowerCase().endsWith(".vmt") && !path.toLowerCase().endsWith(".spr")) {
            path = path + ".vmt";
        }
        if (!path.toLowerCase().startsWith("materials/")) {
            path = "materials/" + path;
        }
        return path;
    }

    private String fixTexture(String origPath, ValueSource src) {
        String path = origPath;
        path = path.replace("\\", "/");
        if (!path.toLowerCase().endsWith(".vtf")) {
            path = path + ".vtf";
        }
        if (!path.toLowerCase().startsWith("materials/")) {
            path = "materials/" + path;
        }
        return path;
    }
    
    private String fixSound(String origPath, ValueSource src) {
        String path = origPath;
        path = path.replace("\\", "/");
        if(!path.contains("/") && !path.toLowerCase().endsWith(".wav")){
            // Probably a symbolic sound, like Ambient.Hum
            return getNonPathPrefix() + origPath;
        }
        if(!path.toLowerCase().endsWith(".wav")){
            path = path + ".wav";
        }
        if(!path.toLowerCase().startsWith("sound/")){
            path = "sound/" + path;
        }
        return path;
    }

    /**
     * Get the string prefix which is added when a non-path value is encountered
     * @return A string prefix
     */
    public String getNonPathPrefix() {
        return nonPathPrefix;
    }

    /**
     * Set the string prefix to be added when a non-path value is encountered
     * @param nonPathPrefix A string to prefix non-paths with, ideally a value
     * which will be unambiguous when seen.
     */
    public void setNonPathPrefix(String nonPathPrefix) {
        this.nonPathPrefix = nonPathPrefix;
    }

    

}
