package com.jgfx.assets.module;

import com.jgfx.assets.naming.Name;
import com.jgfx.assets.naming.Version;
import org.reflections.Reflections;

/**
 * A module is an identified and versioned set of code and/or resources that can be loaded and used at runtime. This class encapsulates information on a
 * module.
 */
public interface Module {
    /**
     * @return The identifier for the module
     */
    Name getId();

    /**
     * @return The version of the module
     */
    Version getVersion();

    /**
     * Provides the partial reflection information for this module, in isolation of other modules.  This information is of limited use by itself - without combining
     * it with the information from its dependencies, it will be unable to resolve subtypes if an intermediate class is missing. Discovered classes will also not be
     * instantiable.
     * <p>
     * Intended for use in building a reflection information for a complete environment.
     * </p>
     *
     * @return The partial reflection information for this module in isolation
     */
    Reflections getReflections();
}

