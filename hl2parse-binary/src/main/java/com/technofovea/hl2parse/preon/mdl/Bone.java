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
/*
 * 
 */

package com.technofovea.hl2parse.preon.mdl;

import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;


/**
 * mstudiobone_t
 * @author Darien Hager
 */
public class Bone {

    @BoundNumber
    protected int nameIndex;

    @BoundNumber
    protected int parentBone;

    @BoundList(size="6")
    protected int[] controllers;

    @Bound
    protected Vector3f pos;

    @Bound
    protected Quaternion4f quat;

    @Bound
    protected Vector3f eulerRot;

    @Bound
    protected Matrix34f pose;

    @Bound
    protected Quaternion4f qAlignment;

    @Bound
    protected int flags;

    @Bound
    protected int procType;

    @Bound
    protected int procIndex;

    @Bound
    protected int physicsbone;

    @Bound
    protected int surfacePropIndex;

    @Bound
    protected int contentFlags;

    @BoundList(size="8")
    protected int[] unused;


}
