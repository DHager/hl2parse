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
package com.technofovea.hl2parse.dmx;

/**
 *
 * @author Darien Hager
 */
public enum DmxAttrDataType {

    ELEMENT(0x01, ElementOffset.class),
    INTEGER(0x02, null),
    FLOAT(0x03, null),
    BOOLEAN(0x04, null),
    STRING(0x05, null),
    BINARY(0x06, null),
    TIME(0x07, null),
    COLOR(0x08, null),
    VECTOR2(0x09, null),
    VECTOR3(0x0A, null),
    VECTOR4(0x0B, null),
    QANGLE(0x0C, null),
    QUATERNION(0x0D, null),
    MATRIX(0x0E, null),
    /*
     * Array versions
     */
    ARRAY_ELEMENT(0x0F, ElementArray.class),
    ARRAY_INTEGER(0x10, INTEGER.storageClass),
    ARRAY_FLOAT(0x11, FLOAT.storageClass),
    ARRAY_BOOLEAN(0x12, BOOLEAN.storageClass),
    ARRAY_STRING(0x13, STRING.storageClass),
    ARRAY_BINARY(0x14, BINARY.storageClass),
    ARRAY_TIME(0x15, TIME.storageClass),
    ARRAY_COLOR(0x16, COLOR.storageClass),
    ARRAY_VECTOR2(0x17, VECTOR2.storageClass),
    ARRAY_VECTOR3(0x18, VECTOR3.storageClass),
    ARRAY_VECTOR4(0x19, VECTOR4.storageClass),
    ARRAY_QANGLE(0x1A, QANGLE.storageClass),
    ARRAY_QUATERNION(0x1B, QUATERNION.storageClass),
    ARRAY_MATRIX(0x1C, MATRIX.storageClass),
    ;
    private final byte typeCode;
    private final Class<? extends Placeholder> storageClass;

    private DmxAttrDataType(int typeCode, Class<? extends Placeholder> storageClass){
        this((byte)typeCode,storageClass);
    }
    private DmxAttrDataType(byte typeCode, Class<? extends Placeholder> storageClass) {
        this.typeCode = typeCode;
        this.storageClass = storageClass;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public Class<? extends Placeholder> getStorageClass() {
        return storageClass;
    }
}
