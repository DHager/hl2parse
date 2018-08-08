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

import com.technofovea.hl2parse.ParseUtil;
import com.technofovea.hl2parse.registry.CdrParser.AppDependency;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author darien.hager
 */
public class ClientRegistryTest {

    static final File BLOB_SOURCE = new File("c:\\Program Files\\Steam\\ClientRegistry.blob");
    static final int EXPECTED_VERSION = 1;
    static final int APPID_HALFLIFE = 70;
    static final int APPID_SOURCE_SDK = 211;
    private Map<Integer, String> cachedAppNames = new HashMap<Integer, String>();

    @Before
    public void setup() throws Exception {
        cachedAppNames.clear();
    }

    @Ignore
    @Test
    public void parseClientRegistry() throws Exception {

        if(!BLOB_SOURCE.exists()){
            throw new IllegalStateException("Cannot test, Steam's clientregistry.blob does not exist on local machine. " +
                    "Due to security concerns, it is not included with distributed code. " +
                    "This test case looks for it in: "+BLOB_SOURCE.getAbsolutePath());
        }
        ByteBuffer buf = ParseUtil.mapFile(BLOB_SOURCE);
        BlobFolder root = RegParser.parseClientRegistry(buf);
        Assert.assertEquals("TopKey", root.getName());
    }

    @Ignore
    @Test
    public void getContentDescriptionRecord() throws BlobParseFailure, IOException {
        ByteBuffer buf = ParseUtil.mapFile(BLOB_SOURCE);
        BlobFolder root = RegParser.parseClientRegistry(buf);
        ClientRegistry cr = new ClientRegistry(root);
        CdrParser cp = cr.getContentDescriptionRecord();
        String name = cp.getAppName(APPID_HALFLIFE);
        Assert.assertEquals("Half-Life", name);

        name = cp.getAppName(APPID_SOURCE_SDK);
        Assert.assertEquals("Source SDK", name);


    }

    @Ignore
    @Test
    public void getAppDependencies() throws Exception {

        ByteBuffer buf = ParseUtil.mapFile(BLOB_SOURCE);
        BlobFolder root = RegParser.parseClientRegistry(buf);
        ClientRegistry cr = new ClientRegistry(root);
        CdrParser cp = cr.getContentDescriptionRecord();

        Set<String> foundNames = new HashSet<String>();
        List<AppDependency> deps = cp.getAppDependencies(APPID_HALFLIFE);
        for (AppDependency i : deps) {
            foundNames.add(cp.getAppName(i.appid));
        }

        Set<String> expectedNames = new HashSet<String>();
        expectedNames.add("Half-Life Base Content");
        expectedNames.add("Base Goldsrc Shared Binaries");
        expectedNames.add("Base Goldsrc Shared Content");
        expectedNames.add("Base Goldsrc Shared Platform");

        Assert.assertEquals(expectedNames, foundNames);

    }

    private String getAppNameCached(CdrParser cp, int id) throws BlobParseFailure {
        if (cachedAppNames.containsKey(id)) {
            return cachedAppNames.get(id);
        }
        String s = cp.getAppName(id);
        cachedAppNames.put(id, s);
        return s;
    }

    @Ignore
    @Test
    public void printAppForWiki() throws Exception {
        Level origLogLevel = org.apache.log4j.Logger.getRootLogger().getLevel();
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);
        ByteBuffer buf = ParseUtil.mapFile(BLOB_SOURCE);
        BlobFolder root = RegParser.parseClientRegistry(buf);
        ClientRegistry cr = new ClientRegistry(root);
        CdrParser cp = cr.getContentDescriptionRecord();

        Set<Integer> appIdBag = new HashSet<Integer>(cp.getAllAppIds());
        Set<Integer> temp = new HashSet<Integer>(appIdBag);
        
        Iterator<Integer> iterScreen = temp.iterator();
        while(iterScreen.hasNext()){
            int id = iterScreen.next();
            List<AppDependency> deps = cp.getAppDependencies(id);
            for (AppDependency dep : deps) {
                appIdBag.remove(dep.appid);

            }
        }

        List<Integer> appIds = new ArrayList<Integer>(appIdBag);
        Collections.sort(appIds);
        int limiter = 0;
        for (int id : appIds) {
            limiter++;
            String name = getAppNameCached(cp, id);
            if (name.startsWith("ValveTestApp")) {
                continue;
            }
            List<AppDependency> deps = cp.getAppDependencies(id);
            Set<AppDependency> tempset = new HashSet<AppDependency>();
            String priString = "* '''" + id + " " + name + "''' ";
            String fname = cp.getAppFolderName(id);
            if(!name.equalsIgnoreCase(fname) && fname != null && (!"".equals(fname))){
                priString += "("+")";
            }
            System.out.println(priString);
            Iterator<AppDependency> iter = deps.iterator();
            while (iter.hasNext()) {
                AppDependency dep = iter.next();
                if (tempset.contains(dep)) {
                    iter.remove();
                } else {
                    tempset.add(dep);
                }
            }
            Collections.sort(deps);
            for (AppDependency dep : deps) {
                if(dep.appid == id){
                    continue;
                }
                String depString = "** " + dep.appid + " " + getAppNameCached(cp, dep.appid);
                if (dep.optional) {
                    depString += " (optional)";
                }
                System.out.println(depString);

            }



            if (limiter > 500) {
                //break;
            }
        }

        org.apache.log4j.Logger.getRootLogger().setLevel(origLogLevel);



    }
}
