package com.jgfx.engine.ecs.entity.ref;

import com.google.common.base.Objects;
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
     *
     * @param componentClass
     * @param <T>
     * @return returns component by type
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        if (isExists()) {
            return getPool().getComponent(getId(), componentClass);
        }
        return null;
    }

    /**
     * Removes a component by the given class type
     *
     * @param componentClass
     * @return returns component by type
     */
    public void removeComponent(Class<? extends Component> componentClass) {
        if (isActive()) {
            getPool().removeComponent(getId(), componentClass);
        }
    }

    /**
     * Adds a component by the given type
     *
     * @param component
     * @param <T>
     * @return
     */
    public <T extends Component> T addComponent(T component) {
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
            var component = getComponent(componentClass);
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
            var component = getComponent(componentClass);
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
    public boolean hasComponent(Class<? extends Component> componentClass) {
        return getComponent(componentClass) != null;
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
