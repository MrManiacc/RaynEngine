package com.jgfx.engine.ecs.entity.builder;

import com.google.common.collect.Maps;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.component.ComponentContainer;
import com.jgfx.engine.ecs.entity.pool.EntityPool;
import com.jgfx.engine.ecs.entity.ref.BaseEntityRef;
import com.jgfx.engine.ecs.entity.ref.EntityRef;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builds an entity with a collection of components
 */
public class EntityBuilder implements ComponentContainer {
    private final Map<Class<? extends Component>, Component> components = Maps.newHashMap();
    private EntityPool pool;
    private Optional<Long> id = Optional.empty();

    public EntityBuilder(EntityPool pool) {
        this.pool = pool;
    }

    /**
     * Check existence of component in container
     *
     * @param component component class to check
     * @return If this has a component of the given type
     */
    @Override
    public boolean hasComponent(Class<? extends Component> component) {
        return components.containsKey(component);
    }

    /**
     * Check existence of any of provided components in container
     *
     * @param filterComponents list of Component classes to check
     * @return If this has at least one component from the list of components
     */
    @Override
    public boolean hasAnyComponents(List<Class<? extends Component>> filterComponents) {
        return !Collections.disjoint(components.keySet(), filterComponents);
    }

    /**
     * Check existence of all provided components in container
     *
     * @param filterComponents list of Component classes to check
     * @return If this has all components from the list of components
     */
    @Override
    public boolean hasAllComponents(List<Class<? extends Component>> filterComponents) {
        return components.keySet().containsAll(filterComponents);
    }

    /**
     * @param componentClass
     * @param <T>
     * @return The requested component, or null if the this doesn't have a component of this type
     */
    @Override
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }

    /**
     * Iterates over all the components this entity has
     *
     * @return
     */
    @Override
    public Iterable<Component> iterateComponents() {
        return components.values();
    }

    /**
     * Adds a component. If this already has a component of the same class it is replaced.
     *
     * @param component
     */
    @Override
    public <T extends Component> T addComponent(T component) {
        components.put(component.getClass(), component);
        return component;
    }

    /**
     * Adds all of the specified components to the entity
     *
     * @param components the entity to add
     */
    public void addComponents(Iterable<? extends Component> components) {
        components = (components == null) ? Collections.EMPTY_LIST : components;
        components.forEach(this::addComponent);
    }

    /**
     * Removes a component from the entity builder
     *
     * @param componentClass
     */
    @Override
    public void removeComponent(Class<? extends Component> componentClass) {
        components.remove(componentClass);
    }

    /**
     * Builds the entity component
     *
     * @return returns the new component
     */
    public EntityRef build() {
        if (id.isPresent() && !pool.registerId(id.get()))
            return EntityRef.NULL;
        long finalId = id.orElse(pool.createEntity());
        pool.insertRef(new BaseEntityRef(pool, finalId), components.values());
        return pool.getEntity(finalId);
    }

    public void setId(long id) {
        this.id = Optional.of(id);
    }
}
