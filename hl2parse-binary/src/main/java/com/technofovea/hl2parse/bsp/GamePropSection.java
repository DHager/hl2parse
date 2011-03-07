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
package com.technofovea.hl2parse.bsp;

import com.technofovea.hl2parse.Vector3f;
import java.nio.ByteBuffer;

/**
 * Parses and encapsulates data for an item in the "props" game-lump, allowing access
 * to information about "static" props which are compiled into
 * the map.
 * 
 * @author Darien Hager
 */
public class GamePropSection {

    private Vector3f origin;
    private Vector3f rotation;
    private int propNameIndex;
    private int firstLeaf;
    private int leafCount;
    private byte solid;
    private byte flags;
    private int skin;
    private float fadeMinDist;
    private float fadeMaxDist;
    private Vector3f lightingOrigin;
    private float forcedFadeScale = 0f;
    private byte[] unknown1 = new byte[4];

    /**
     * Creates a new game-prop entry by pulling data from the given buffer.
     *
     * @param hdr The header file to use for version information
     * @param buf The game-lump data to parse from. When finished the buffer's
     * position should already be advanced to the next prop or to the end.
     */
    public GamePropSection(GameLumpHeader hdr, ByteBuffer buf) {

        origin = new Vector3f(buf);
        rotation = new Vector3f(buf); // Actually a euler rotation
        propNameIndex = buf.getShort();
        firstLeaf = buf.getShort();
        leafCount = buf.getShort();
        solid = buf.get();
        flags = buf.get();

        skin = buf.getInt();
        fadeMinDist = buf.getFloat();
        fadeMaxDist = buf.getFloat();

        lightingOrigin = new Vector3f(buf);
        if (hdr.getVersion() >= 5) {
            forcedFadeScale = buf.getFloat();
        }
        if (hdr.getVersion() >= 6) {
            buf.get(unknown1);
        }
    }

    /**
     * Gets the maximum fade distance
     * @return The distance at which this prop completely fades out
     */
    public float getFadeMaxDist() {
        return fadeMaxDist;
    }

    /**
     * Gets the minimum fade distance
     * @return The distance at which this prop begins to fade out.
     */
    public float getFadeMinDist() {
        return fadeMinDist;
    }

    public int getFirstLeaf() {
        return firstLeaf;
    }

    /**
     * Retrieves any flags associated with this prop as a byte
     * @return A byte representing eight boolean flags.
     */
    public byte getFlags() {
        return flags;
    }

    public float getForcedFadeScale() {
        return forcedFadeScale;
    }

    public int getLeafCount() {
        return leafCount;
    }

    public Vector3f getLightingOrigin() {
        return lightingOrigin;
    }

    /**
     * Get the origin of the prop
     * @return The origin in X,Y,Z coordinates
     */
    public Vector3f getOrigin() {
        return origin;
    }

    /**
     * Get the index number used for identifying this prop among the list of
     * names embedded elsewhere in the file.
     * @return The index ID for this prop within a map
     */
    public int getPropNameIndex() {
        return propNameIndex;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Get the skin ID associated with this prop
     * @return A zero-indexed skin ID
     */
    public int getSkin() {
        return skin;
    }

    public byte getSolid() {
        return solid;
    }

    public byte[] getUnknown1() {
        return unknown1;
    }
}
