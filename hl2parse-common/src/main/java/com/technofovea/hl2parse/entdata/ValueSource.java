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
 * Represents a source for entity values.
 */
public class ValueSource {

    /**
     * The entity-class to get values from
     */
    String className;
    /**
     * The property-name on the entity to get a value from
     */
    String propertyName;
    /**
     * The type (typically specified by FGD) of the value(s)
     */
    String dataType;



    /**
     * Creates a new object with the given class/property names and datatype.
     * @param className The class-name
     * @param propertyName The property-name
     * @param dataType The datatype for the property
     */
    public ValueSource(String className, String propertyName, String dataType) {
        //TODO investigate how case-sensitivity comes into play
        this.className = className;
        this.propertyName = propertyName;
        this.dataType = dataType;
    }




    /**
     * Gets the entity class-name of this source
     * @return An entity class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * The name of the property on the entity which is being targeted
     * @return A string property name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Gets the data type associated with the property values
     * @return A string for the datatype, as used in FGDs
     */
    public String getDataType() {
        return dataType;
    }


    

    @Override
    public String toString() {
        return "{" + className + "," + propertyName + ":" + dataType + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValueSource other = (ValueSource) obj;
        if ((this.className == null) ? (other.className != null) : !this.className.equalsIgnoreCase(other.className)) {
            return false;
        }
        if ((this.propertyName == null) ? (other.propertyName != null) : !this.propertyName.equalsIgnoreCase(other.propertyName)) {
            return false;
        }
        if ((this.dataType == null) ? (other.dataType != null) : !this.dataType.equalsIgnoreCase(other.dataType)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.className != null ? this.className.hashCode() : 0);
        hash = 97 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        hash = 97 * hash + (this.dataType != null ? this.dataType.hashCode() : 0);
        return hash;
    }
}
