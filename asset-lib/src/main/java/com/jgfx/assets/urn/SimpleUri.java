package com.jgfx.assets.urn;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.jgfx.assets.naming.Name;

public class SimpleUri implements Uri, Comparable<SimpleUri> {
    private Name moduleName = Name.EMPTY;
    private Name objectName = Name.EMPTY;

    /**
     * Creates an empty, invalid SimpleUri
     */
    public SimpleUri() {
    }

    /**
     * Creates a SimpleUri for the given module:object combo
     */
    public SimpleUri(String moduleName, String objectName) {
        this(new Name(moduleName), new Name(objectName));
    }

    /**
     * Creates a SimpleUri for the given module:object combo
     */
    public SimpleUri(Name moduleName, String objectName) {
        this(moduleName, new Name(objectName));
    }

    /**
     * Creates a SimpleUri for the given module:object combo
     */
    public SimpleUri(Name moduleName, Name objectName) {
        Preconditions.checkNotNull(moduleName);
        Preconditions.checkNotNull(objectName);
        this.moduleName = moduleName;
        this.objectName = objectName;
    }

    /**
     * Creates a SimpleUri from a string in the format "module:object". If the string does not match this format, it will be marked invalid
     */
    public SimpleUri(String simpleUri) {
        String[] split = simpleUri.split(MODULE_SEPARATOR, 2);
        if (split.length > 1) {
            moduleName = new Name(split[0]);
            objectName = new Name(split[1]);
        }
    }

    @Override
    public Name getModuleName() {
        return moduleName;
    }

    public Name getObjectName() {
        return objectName;
    }

    @Override
    public boolean isValid() {
        return !moduleName.isEmpty() && !objectName.isEmpty();
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "";
        }
        return moduleName + MODULE_SEPARATOR + objectName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SimpleUri) {
            SimpleUri other = (SimpleUri) obj;
            return Objects.equal(moduleName, other.moduleName) && Objects.equal(objectName, other.objectName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(moduleName, objectName);
    }

    @Override
    public int compareTo(SimpleUri o) {
        int result = moduleName.compareTo(o.getModuleName());
        if (result == 0) {
            result = objectName.compareTo(o.getObjectName());
        }
        return result;
    }
}
