package com.jgfx.gui.helpers;

/**
 * Simple helper for naming
 */
public interface IEasyName {

    /**
     * @return returns the classes name, used for convince, it's lower case and unqualified by default
     */
    default String name() {
        return name(true);
    }

    /**
     * @return returns the classes name, used for convince
     */
    default String name(boolean lower) {
        return name(lower, false, true);
    }

    /**
     * If it's qualified then we want the package with the name
     *
     * @return returns the classes name, used for convince
     */
    default String name(boolean lower, boolean qualified, boolean removeExtension) {
        var name = qualified ? getClass().getName() : getClass().getSimpleName();
        if (removeExtension)
            name = name.replaceAll(lower ? "cmp$" : "Cmp$", "");
        return lower ? name.toLowerCase() : name;
    }
}
