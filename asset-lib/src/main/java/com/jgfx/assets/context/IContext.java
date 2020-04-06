package com.jgfx.assets.context;

import com.jgfx.assets.urn.ResourceUrn;

/**
 * Provides classes with the utility objects that belong to the context they are running in.
 * <p>
 * Use dependency injection or this interface to get at the objects you want to use.
 * <p>
 * <p>
 * From this class there can be multiple instances. For example we have the option of letting a client and server run
 * concurrently in one VM, by letting them work with two separate context objects.
 * <p>
 * This class is intended to replace the CoreRegistry and other static means to get utility objects.
 * <p>
 * Contexts must be thread safe!
 */
public interface IContext {

    /**
     * @return the object that is known in this context for this type.
     */
    <T> T get(Class<? extends T> type);


    /**
     * @return the object that is known in this context for this type.
     */
    <T> T get(ResourceUrn resourceUrn, Class<? extends T> type);

    /**
     * @return the object that is known in this context for this type.
     */
    default <T> T get(String resourceUrn, Class<? extends T> type) {
        return get(new ResourceUrn(resourceUrn), type);
    }


    /**
     * Makes the object known in this context to be the object to work with for the given type.
     */
    void put(Class<?> clazz, Object object);

    /**
     * Makes the object known in this context to be the object to work with for the given type.
     */
    void put(Object object);

    /**
     * Puts an object with the given urn type, into the named map
     */
    void put(ResourceUrn resourceUrn, Object object);

    /**
     * Puts an object with the given urn type, into the named map
     */
    default void put(String resourceUrn, Object object) {
        put(new ResourceUrn(resourceUrn), object);
    }
}
