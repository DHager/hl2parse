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
package com.technofovea.hl2parse.preon.mdl;

import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundObject;
import org.codehaus.preon.annotation.BoundString;

/**
 *
 * @author Darien Hager
 */
public class Mdl {

    @BoundString(match = "IDST", size = "4")
    protected String magicHeader;
    @BoundNumber
    protected int version;
    @BoundNumber
    protected int checksum;
    @BoundString(size = "64")
    protected String modelName;
    @BoundNumber
    protected int dataLength;
    @BoundObject
    protected Vector3f eyePosition;
    @BoundObject
    protected Vector3f illumposition;
    @BoundObject
    protected Vector3f hull_min;
    @BoundObject
    protected Vector3f hull_max;
    @BoundObject
    protected Vector3f view_bbmin;
    @BoundObject
    protected Vector3f view_bbmax;

    @BoundList(size="4")
    protected byte[] flags; //FIXME: Use boolean or bitset



    @BoundNumber
    protected int boneCount;
    @BoundNumber
    protected int boneIndex;

    @BoundList(offset="boneIndex",size="boneCount")
    protected Bone[] bones;

    

}
