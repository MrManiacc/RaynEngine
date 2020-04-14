package com.jgfx.engine.ecs.entity.ref;

import com.google.common.base.Objects;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.ecs.World;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.entity.pool.EntityPool;

import java.util.Collections;
import java.util.function.Consumer;

/**
 * This class is used to streamline the use of entities, via a reference
 */
public abstract class EntityRef {
    public static final EntityRef NULL = null;

    /**
     * Copies this entity, creating a new entity with identical components.
     * Note: You will need to be careful when copying entities, particularly around ownership - this method does nothing to prevent you ending up
     * with multiple entities owning the same entities.
     *
     * @return A copy of this entity.
     */
    public abstract EntityRef copy();

    /**
     * @return Does this entity exist - that is, is not deleted.
     */
    public abstract boolean isExists();

    /**
     * @return Whether this entity is currently loaded (not stored)
     */
    public abstract boolean isActive();

    /**
     * Removes all components and destroys it
     */
    public abstract void dispose();

    /**
     * @return The identifier of this entity. Should be avoided where possible and the EntityRef
     * used instead to allow it to be invalidated if the entity is destroyed.
     */
    public abstract long getId();

    /**
     * @return A full, json style description of the entity.
     */
    public abstract String toJson();


    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EntityRef) {
            EntityRef other = (EntityRef) o;
            return !isExists() && !other.isExists() || getId() == other.getId();
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getId());
    }

    /**
     * Gets a component by the given class
     * by default we'll check the super class and attempt to do some casting
     *
     * @return returns component by type
     */
    public <T extends Component> T get(Class<T> componentClass) {
        return get(componentClass, true);
    }

    /**
     * Gets a component by the given class
     *
     * @param componentClass
     * @param <T>
     * @return returns component by type
     */
    public <T extends Component> T get(Class<T> componentClass, boolean useParent) {
        if (isExists()) {
            if (!useParent)
                return getPool().getComponent(getId(), componentClass);
            var parentComponentClass = getSuperComponent(componentClass);
            var parentComponent = getPool().getComponent(getId(), parentComponentClass);
            if (parentComponent != null && parentComponentClass.isAssignableFrom(componentClass)) {
                return componentClass.cast(parentComponent);
            }
        }
        return null;
    }

    /**
     * Removes a component by the given class type
     *
     * @param componentClass
     * @return returns component by type
     */
    public void remove(Class<? extends Component> componentClass) {
        if (isActive()) {
            getPool().removeComponent(getId(), componentClass);
        }
    }

    /**
     * Removes all of the components from the entity
     */
    public void removeAll() {
        if (isActive())
            for (var component : iterateComponents())
                remove(component.getClass());
    }

    /**
     * Adds a component by the given type
     *
     * @param component
     * @param <T>
     * @return
     */
    public <T extends Component> T add(T component) {
        if (isActive()) {
            return getPool().addComponent(getId(), component);
        }
        return component;
    }

    /**
     * Gets an optional of the component, which is useful for inline checking/consumers etc
     *
     * @param componentClass
     * @param <T>
     * @return
     */
    public <T extends Component> void ifPresent(Class<T> componentClass, Consumer<T> consumer) {
        if (isExists()) {
            var component = get(componentClass);
            if (component != null)
                consumer.accept(component);
        }
    }

    /**
     * Gets an optional of the component, which is useful for inline checking/consumers etc
     *
     * @param componentClass
     * @param <T>
     * @return
     */
    public <T extends Component> void ifPresentOrElse(Class<T> componentClass, Consumer<T> consumer, Runnable runnable) {
        if (isExists()) {
            var component = get(componentClass);
            if (component != null)
                consumer.accept(component);
            else
                runnable.run();
        }
    }

    /**
     * Checks to see if this entity has the given component
     *
     * @return returns true if component is present
     */
    public boolean has(Class<? extends Component> componentClass) {
        return has(componentClass, true);
    }

    /**
     * Checks to see if this entity has the given component
     *
     * @return returns true if component is present
     */
    public boolean has(Class<? extends Component> componentClass, boolean checkSuper) {
        if (!checkSuper) {
            return get(componentClass, false) != null;
        } else {
            if (get(componentClass, false) != null)
                return true;
            var superClass = componentClass.getSuperclass();
            if (superClass != null && Component.class.isAssignableFrom(superClass)) {
                var superComponent = (Class<? extends Component>) superClass;
                var component = get(superComponent);
                if (component != null) {
                    return component.getClass().isAssignableFrom(componentClass);
                }
            }
        }
        return false;
    }

    /**
     * @return returns the correct component class, will first check
     */
    private Class<? extends Component> getSuperComponent(Class<? extends Component> component) {
        if (has(component, false))
            return component;
        if (has(component, true)) {
            var superClass = component.getSuperclass();
            if (superClass != null && Component.class.isAssignableFrom(superClass)) {
                return (Class<? extends Component>) superClass;
            }
        }
        return component;
    }

    /**
     * Iterates the components for the given entity
     *
     * @return
     */
    public Iterable<Component> iterateComponents() {
        if (isExists()) {
            return getPool().getComponentStore().iterateComponents(getId());
        }
        return Collections.emptyList();
    }

    protected abstract EntityPool getPool();

}
