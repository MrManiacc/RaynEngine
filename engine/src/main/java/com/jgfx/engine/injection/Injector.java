package com.jgfx.engine.injection;

import com.google.common.eventbus.Subscribe;
import com.jgfx.engine.injection.anotations.In;


/**
 * Represents an injection type
 */
public enum Injector {
    /**
     * Class fields marked with {@link In} will be injected from the {@link CoreContext}
     */
    GENERICS(0),
    /**
     * Registers the the given class with the specified {@link Bus} event bus.
     * This allows for use of {@link Subscribe} to be registered to the correct bus.
     * When using multiple event buses, the subscriptions with the same events
     * will be called from all the specified buses
     */
    SUBSCRIBERS(1),
    /**
     * Injects all fields with {@link All}, {@link One}, or {@link Exclude}.
     * This must be used only after the {@link EntityManager} has been initialized, which happens very early.
     */
    GROUPS(2),
    /**
     * This is used to inject all fields marked with {@link Single} with the correct entity.
     * This must be used only after the {@link EntityManager} has been initialized, which happens very early.
     */
    SINGLES(3),
    /**
     * This is used to inject all fields with {@link Resource}. It will take the string value,
     * and convert it into a urn, then map it using the {@link AssetManager#getAsset(String, Class)}.
     * The class type will be inferred from the field type.
     */
    ASSETS(4),
    /**
     * Injects all of the above types into the given object.
     * This is mainly only going to be used inside an instance of {@link EntitySystem},
     * other things like a subsystem should be sandboxed and only inject what is required
     */
    ALL(5);

    private int id;

    Injector(int id) {
        this.id = id;
    }

    /**
     * Injects the object with the specified type based on the {@link Injector} type
     * TODO: log the injection
     *
     * @param injectable the object to inject
     */
    public void inject(Object injectable) {
        inject(injectable, false);
    }

    /**
     * Injects the object with the specified type based on the {@link Injector} type.
     * It will ignore the exceptions
     * TODO: log the injection
     *
     * @param injectable the object to inject
     */
    public void inject(Object injectable, boolean ignoreExceptions) {
        try {
            switch (id) {
                case 0:
                    InjectionHelper.injectGenerics(injectable);
                    break;
                case 1:
                    InjectionHelper.injectSubscribers(injectable);
                    break;
                case 2:
                    InjectionHelper.injectGroups(injectable);
                    break;
                case 3:
                    InjectionHelper.injectSingles(injectable);
                    break;
                case 4:
                    InjectionHelper.injectAssets(injectable);
                    break;
                case 5:
                    InjectionHelper.injectGenerics(injectable);
                    InjectionHelper.injectSubscribers(injectable);
                    InjectionHelper.injectGroups(injectable);
                    InjectionHelper.injectSingles(injectable);
                    InjectionHelper.injectAssets(injectable);
                    break;
            }
        } catch (InjectionException e) {
            if (!ignoreExceptions)
                e.printStackTrace();
        }
    }
}
