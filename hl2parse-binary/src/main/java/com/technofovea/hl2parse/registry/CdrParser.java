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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class CdrParser {

    private static final Logger logger = LoggerFactory.getLogger(CdrParser.class);
    
    public static final int Zone_Applications = 1;
    static final int BYTE_TRUE = 0x01;
    static final int IDX_APP = 1;
    static final int IDX_NAME = 2;
    static final int IDX_FOLDER = 3;
    static final int IDX_LAUNCH = 6;

    ByteBuffer core;

    public static class AppDependency implements Comparable<AppDependency> {

        public int appid = -1;
        public boolean optional = true;
        public String mountName = "";
        public String operatingSystem = null;

        public int compareTo(AppDependency o) {
            return this.appid - o.appid;
        }


    }

    public CdrParser(ByteBuffer data) {
        core = data;
    }

    /**
     * 0 Version
     * 1 Applications
     *      1 appid
     *      2 name
     *      3 installdirname
     *      4 mincachemb
     *      5 maxcachemb
     *      6 launchoptions (cell)
     *      7 appicons (cell?)
     *      8 firstlaunch
     *      9 bandwidthpolicy
     *      10 app versions (cell)
     *      11 currentversion id
     *      12 FilesystemsRecord (cell)
     *      13 trickle version
     *      14 AppUserDefinedRecord
     *      15 beta pass
     *      16 beta version id
     *      17 legacy install dir
     *      18 SkipMFPOverwrite
     *      19 UseFilesystemDvr
     *      20 gcf-only app
     *      21 AppOfManifestOnlyCache
     *
     * 2 Subscriptions
     * 3 LastChangedExistingAppOrSubscriptionTime
     * 4 ??
     * 5 AllAppsPublicKeysRecord
     */
    static ByteBuffer getData(ByteBuffer current, Integer... indices) throws BlobParseFailure {
        logger.trace("Retrieving nested index-based values from content data description: {}",Arrays.toString(indices));
        current = current.asReadOnlyBuffer();
        current.order(ByteOrder.LITTLE_ENDIAN);
        current.clear();

        for (int j=0; j < indices.length; j++) {
            int index = indices[j];
            CellCollection cd = RegParser.parseCell(current);
            boolean found = false;
            for (CellItem ci : cd) {
                int curIndex = ci.getMeta().getInt();
                if (index == curIndex) {
                    // Change current and break out of the inner loop
                    current = ci.getPayload();
                    found = true;
                    break;
                }
            }
            if (found) {
                continue;
            } else {                
                throw new BlobParseFailure("Unable to find last index in sequence " + Arrays.copyOf(indices, j));
            }
        }

        return current;

    }

    public ByteBuffer getData(Integer... indices) throws BlobParseFailure {
        return getData(core, indices);
    }

    public List<AppDependency> getAppDependencies(int appid) throws BlobParseFailure {
        logger.debug("Getting app dependencies for app {}",appid);
        List<AppDependency> ret = new ArrayList<AppDependency>();
        ByteBuffer fsrBlob = getData(Zone_Applications, appid, 12);

        CellCollection cd = RegParser.parseCell(fsrBlob);
        for (CellItem ci : cd) {
            CellCollection cd2 = RegParser.parseCell(ci.getPayload());
            AppDependency ad = new AppDependency();
            if (cd2.items.size() < 3 || cd2.items.size() >4) {
                throw new BlobParseFailure("App filesystem entries are expected to have 3 or 4 sections. Found " + cd2.items.size() + " instead.");
            }
            for (CellItem ci2 : cd2) {
                int i = ci2.getMeta().getInt();
                switch (i) {
                    case 1:
                        // App ID of the item
                        ad.appid = ci2.getPayload().getInt();
                        break;
                    case 2:
                        // The name for the mounted item
                        ad.mountName = RegParser.getText(ci2.getPayload());
                        break;
                    case 3:
                        // Whether the item is optional
                        ad.optional = (ci2.getPayload().get() == BYTE_TRUE);
                        break;
                    case 4:
                        // Operating system string. "windows" vs "macos", right now.
                        ad.operatingSystem = RegParser.getText(ci2.getPayload());
                        break;
                    default:
                        throw new BlobParseFailure("App filesystem entry found with unexpected section number "+i);
                }

            }
            ret.add(ad);

        }
        return ret;
    }

    public List<Integer> getAllAppIds() throws BlobParseFailure{
        List<Integer> ret = new ArrayList<Integer>();
        ByteBuffer appBuf = getData(IDX_APP);

        CellCollection cc = RegParser.parseCell(appBuf);
        for (CellItem ci : cc) {
            int id = ci.getMeta().getInt();
            ret.add(id);
        }
        return ret;

    }

    public String getAppName(int appid) throws BlobParseFailure {
        logger.debug("Getting app name for app {}",appid);
        List<Integer> ret = new ArrayList<Integer>();
        ByteBuffer nameBuf = getData(IDX_APP, appid, IDX_NAME);

        return RegParser.getText(nameBuf);
    }

    public String getAppFolderName(int appid) throws BlobParseFailure {
        logger.debug("Getting app folder name for app {}",appid);
        ByteBuffer nameBuf = getData(IDX_APP, appid, IDX_FOLDER);

        return RegParser.getText(nameBuf);
    }

    public Map<String, String> getLaunchCommands(int appid) throws BlobParseFailure {
        logger.debug("Getting launch commands for app {}",appid);
        Map<String, String> ret = new HashMap<String, String>();
        ByteBuffer launchConfigs = getData(IDX_APP, appid, IDX_LAUNCH);

        CellCollection cd = RegParser.parseCell(launchConfigs);
        for (CellItem ci : cd) {
            int launchNum = ci.getMeta().getInt();
            String name = RegParser.getText(getData(ci.getPayload(), 1));
            String cmdline = RegParser.getText(getData(ci.getPayload(), 2));

            ret.put(name, cmdline);
        }
        return ret;
    }
}
