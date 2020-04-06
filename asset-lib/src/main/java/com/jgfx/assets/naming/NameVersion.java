package com.jgfx.assets.naming;

import java.util.Objects;

/**
 * A combined name and version, for uniquely identifying something by Name and Version.
 * <p>
 * Immutable.
 * </p>
 *
 * @author Immortius
 */
public class NameVersion {
    private final Name name;
    private final Version version;

    public NameVersion(Name name, Version version) {
        this.name = name;
        this.version = version;
    }

    public Name getName() {
        return name;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return name + ":" + version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof NameVersion) {
            NameVersion other = (NameVersion) obj;
            return Objects.equals(name, other.name) && Objects.equals(version, other.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, name);
    }
}