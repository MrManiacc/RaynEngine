package com.jgfx.engine.injection;


import com.jgfx.assets.Asset;
import com.jgfx.assets.context.Context;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.type.AssetManager;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.entity.pool.EntityManager;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.ecs.group.GroupBuilder;
import com.jgfx.engine.event.Bus;
import com.jgfx.engine.injection.anotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles injections into objects
 */
public class InjectionHelper {
    private static final Logger logger = LogManager.getLogger(InjectionHelper.class);

    private InjectionHelper() {
    }

    /**
     * Injects a generic type of class to the given field values that have the @In annotation
     *
     * @param object  object to inject
     * @param context the context to use
     */
    static void injectGenerics(final Object object, Context context) throws InjectionException {
        var errorMessage = new AtomicReference<Optional<String>>(Optional.empty());
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            for (Field field : ReflectionUtils.getAllFields(object.getClass(), ReflectionUtils.withAnnotation(In.class))) {
                Object value = context.get(field.getType());
                if (value != null) {
                    try {
                        field.setAccessible(true);
                        field.set(object, value);
                    } catch (IllegalAccessException e) {
                        errorMessage.set(Optional.of("Illegal access, failed to inject to object '" + object.getClass().getName() + "'."));
                        break;
                    }
                } else
                    errorMessage.set(Optional.of("Failed to inject into object '" + object.getClass().getName() + "', The value with the type of '" + field.getType().getName() + "' was not found in the registry."));
            }

            return null;
        });
        if (errorMessage.get().isPresent())
            throw new InjectionException(errorMessage.get().get());
    }

    /**
     * Injects an object from the registry to any class that has the In annotation
     *
     * @param object
     */
    static void injectGenerics(final Object object) throws InjectionException {
        var errorMessage = new AtomicReference<Optional<String>>(Optional.empty());
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            for (Field field : ReflectionUtils.getAllFields(object.getClass(), ReflectionUtils.withAnnotation(In.class))) {
                var value = CoreContext.get(field.getType());
                if (value != null) {
                    try {
                        field.setAccessible(true);
                        field.set(object, value);
                    } catch (IllegalAccessException e) {
                        errorMessage.set(Optional.of("Illegal access, failed to inject to object '" + object.getClass().getName() + "'."));
                        break;
                    }
                } else
                    errorMessage.set(Optional.of("Failed to inject into object '" + object.getClass().getName() + "', The value with the type of '" + field.getType().getName() + "' was not found in the registry."));

            }

            return null;
        });
        if (errorMessage.get().isPresent())
            throw new InjectionException(errorMessage.get().get());
    }

    /**
     * Injects all of the given group objects to the entity
     *
     * @param object
     */
    static void injectGroups(final Object object) throws InjectionException {
        var errorMessage = new AtomicReference<Optional<String>>(Optional.empty());
        var entityManager = CoreContext.get(EntityManager.class);
        if (entityManager != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                var cls = object.getClass();
                var fields = cls.getDeclaredFields();
                for (var field : fields) {
                    if (field.getType().equals(Group.class)) {
                        field.setAccessible(true);
                        var builder = new GroupBuilder();
                        if (field.isAnnotationPresent(All.class)) {
                            var allTypes = field.getDeclaredAnnotationsByType(All.class);
                            for (var all : allTypes)
                                builder.all(all.value());
                        }
                        if (field.isAnnotationPresent(One.class)) {
                            var oneTypes = field.getDeclaredAnnotationsByType(One.class);
                            for (var one : oneTypes)
                                builder.one(one.value());
                        }

                        if (field.isAnnotationPresent(Exclude.class)) {
                            var excludeTypes = field.getDeclaredAnnotationsByType(Exclude.class);
                            for (var oneExclude : excludeTypes)
                                builder.exclude(oneExclude.value());
                        }
                        try {
                            field.set(object, builder.build(/*entityManager*/));
                        } catch (IllegalAccessException e) {
                            errorMessage.set(Optional.of("Illegal access, failed to inject to object '" + object.getClass().getName() + "'."));
                            break;
                        }
                    }
                }
                return null;
            });
        } else
            errorMessage.set(Optional.of("Failed to find '" + EntityManager.class.getName() + "' inside the core registry."));

        if (errorMessage.get().isPresent())
            throw new InjectionException(errorMessage.get().get());

    }

    /**
     * Registers the class with the correct EventBus's
     */
    static void injectSubscribers(final Object object) {
        //A helper method to register an event system to the given bus
        var cls = object.getClass();
        if (cls.isAnnotationPresent(EventSubscriber.class)) {
            var subscription = cls.getDeclaredAnnotation(EventSubscriber.class);
            for (Bus bus : subscription.value()) {
                bus.register(object);
                logger.debug("Injected subscribable into {} with bus {}", object.getClass().getSimpleName(), bus.name());
            }

        }
    }

    /**
     * Injects the assets to the mapped asset values
     *
     * @param object
     * @throws InjectionException
     */
    static void injectAssets(final Object object) throws InjectionException {
        var errorMessage = new AtomicReference<Optional<String>>(Optional.empty());
        var assetManager = CoreContext.get(AssetManager.class);
        if (assetManager != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                for (Field field : ReflectionUtils.getAllFields(object.getClass(), ReflectionUtils.withAnnotation(Resource.class))) {
                    var assetAnnotation = field.getDeclaredAnnotation(Resource.class);
                    var urn = new ResourceUrn(assetAnnotation.value());
                    if (isSubclassOf(field.getType(), Asset.class)) {
                        var asset = assetManager.getAsset(urn, (Class<? extends Asset>) field.getType());
                        if (asset.isPresent()) {
                            try {
                                field.setAccessible(true);
                                field.set(object, asset.get());
                            } catch (IllegalAccessException e) {
                                errorMessage.set(Optional.of("Illegal access, failed to inject to object '" + object.getClass().getName() + "'."));
                                break;
                            }
                        } else {
                            errorMessage.set(Optional.of("Failed to find asset '" + urn + "' in object '" + object.getClass().getName()));
                            break;
                        }
                    } else {
                        errorMessage.set(Optional.of("Failed to set asset '" + urn + "' in object '" + object.getClass().getName() + "' because the field is not a child of Asset."));
                        break;
                    }
                }
                return null;
            });
        } else
            errorMessage.set(Optional.of("Failed to inject into assets into '" + object.getClass().getName() + "', the '" + AssetManager.class.getName() + "' was not found in the core registry"));
        if (errorMessage.get().isPresent())
            throw new InjectionException(errorMessage.get().get());
    }

    /**
     * Injects the class with the correct single entities that are registers with the entity manager
     */
    static void injectSingles(final Object object) throws InjectionException {
        var manager = CoreContext.get(EntityManager.class);
        var errorMessage = new AtomicReference<Optional<String>>(Optional.empty());
        if (manager != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                for (Field field : ReflectionUtils.getAllFields(object.getClass(), ReflectionUtils.withAnnotation(Single.class))) {
                    var singleEntity = field.getDeclaredAnnotation(Single.class);
                    var urn = new ResourceUrn(singleEntity.value());
                    var entity = manager.getSingleEntity(urn);
                    if (entity != null) {
                        try {
                            field.setAccessible(true);
                            if (EntityRef.class.isAssignableFrom(field.getType())) {
                                field.set(object, entity);
                            } else if (Component.class.isAssignableFrom(field.getType())) {
                                //We're trying to map a component
                                var componentType = (Class<? extends Component>) field.getType();
                                if (entity.has(componentType)) {
                                    var component = entity.get(componentType);
                                    field.set(object, component);
                                }
                            }
                        } catch (IllegalAccessException e) {
                            errorMessage.set(Optional.of("Illegal access, failed to inject to object '" + object.getClass().getName() + "'."));
                            break;
                        }
                    } else {
                        errorMessage.set(Optional.of("Failed to find entity with urn '" + urn + "'."));
                        break;
                    }
                }
                return null;
            });
        } else
            errorMessage.set(Optional.of("Failed to find '" + EntityManager.class.getName() + "' inside the core registry."));

        if (errorMessage.get().isPresent())
            throw new InjectionException(errorMessage.get().get());
    }

    /**
     * Checks if an object is of type of super class
     *
     * @param clazz      the class to check
     * @param superClass the super class to check against
     * @return returns true if subclass
     */
    private static boolean isSubclassOf(Class<?> clazz, Class<?> superClass) {
        if (superClass.equals(Object.class)) {
            // Every class is an Object.
            return true;
        }
        if (clazz.equals(superClass)) {
            return true;
        } else {
            clazz = clazz.getSuperclass();
            // every class is Object, but superClass is below Object
            if (clazz.equals(Object.class)) {
                // we've reached the top of the hierarchy, but superClass couldn't be found.
                return false;
            }
            // try the next level up the hierarchy.
            return isSubclassOf(clazz, superClass);
        }
    }
}
