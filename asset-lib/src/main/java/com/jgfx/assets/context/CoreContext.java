package com.jgfx.assets.context;

import com.jgfx.assets.urn.ResourceUrn;

/**
 * This class is the backend for the engine. It will keep track of all of the needed variables, and allows for injections
 */
public final class CoreContext {
    private static final Context coreContext = new Context();

    private CoreContext() {
    }

    /**
     * Registers an object. These objects will be removed when CoreRegistry.clear() is called (typically when game state changes)
     *
     * @param object The system itself
     */
    public static <T, U extends T> U put(U object) {
        coreContext.put(object);
        return object;
    }

    /**
     * Registers an object. These objects will be removed when CoreRegistry.clear() is called (typically when game state changes)
     *
     * @param object The system itself
     */
    public static <T, U extends T> U put(Class<T> clazz, U object) {
        coreContext.put(clazz, object);
        return object;
    }

    /**
     * Registers an object. These objects will be removed when CoreRegistry.clear() is called (typically when game state changes)
     *
     * @param object      The system itself
     * @param resourceUrn the urn to put
     */
    public static <T, U extends T> U put(String resourceUrn, U object) {
        coreContext.put(resourceUrn, object);
        return object;
    }

    /**
     * Registers an object. These objects will be removed when CoreRegistry.clear() is called (typically when game state changes)
     *
     * @param object      The system itself
     * @param resourceUrn the urn to put
     */
    public static <T, U extends T> U put(ResourceUrn resourceUrn, U object) {
        coreContext.put(resourceUrn, object);
        return object;
    }


    /**
     * @return The system fulfilling the given interface
     */
    public static <T> T get(Class<T> type) {
        if (type == Context.class || type == IContext.class)
            return type.cast(coreContext);
        return coreContext.get(type);
    }

    /**
     * @return The system fulfilling the given interface
     */
    public static <T> T get(ResourceUrn resourceUrn, Class<T> type) {
        if (type == Context.class || type == IContext.class)
            return type.cast(coreContext);
        return coreContext.get(resourceUrn, type);
    }


    /**
     * @return The system fulfilling the given interface
     */
    public static <T> T get(String resourceUrn, Class<T> type) {
        if (type == Context.class || type == IContext.class)
            return type.cast(coreContext);
        return coreContext.get(resourceUrn, type);
    }

    /**
     * Checks to see if the class is present
     *
     * @return returns true if present
     */
    public static boolean has(Class<?> type) {
        return get(type) != null;
    }


    /**
     * Checks to see if the class is present
     *
     * @return returns true if present
     */
    public static boolean has(ResourceUrn resourceUrn, Class<?> type) {
        return get(resourceUrn, type) != null;
    }


    /**
     * Checks to see if the class is present
     *
     * @return returns true if present
     */
    public static boolean has(String resourceUrn, Class<?> type) {
        return get(resourceUrn, type) != null;
    }


}
