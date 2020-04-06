package com.jgfx.engine.ecs.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.List;
import java.util.Map;

/**
 * A table for storing entities and components. Focused on allowing iteration across a components of a given type
 */
public class ComponentTable {
    private Map<Class<?>, TLongObjectMap<Component>> store = Maps.newConcurrentMap();

    /**
     * Gets a given component from the specified entity if it exists
     *
     * @param entityId
     * @param componentClass
     * @param <T>
     * @return returns the component
     */
    public <T extends Component> T get(long entityId, Class<T> componentClass) {
        TLongObjectMap<Component> entityMap = store.get(componentClass);
        if (entityMap != null) {
            return componentClass.cast(entityMap.get(entityId));
        }
        return null;
    }

    /**
     * Puts a component into the map with the specified entity id
     *
     * @param entityId
     * @param component
     * @return returns the component
     */
    public Component put(long entityId, Component component) {
        TLongObjectMap<Component> entityMap = store.get(component.getClass());
        if (entityMap == null) {
            entityMap = new TLongObjectHashMap<>();
            store.put(component.getClass(), entityMap);
        }
        return entityMap.put(entityId, component);
    }

    /**
     * @return removes the component with the specified class from the entity and returns it.
     * Returns null if no component could be removed.
     */
    public <T extends Component> Component remove(long entityId, Class<T> componentClass) {
        TLongObjectMap<Component> entityMap = store.get(componentClass);
        if (entityMap != null) {
            return entityMap.remove(entityId);
        }
        return null;
    }

    /**
     * Clears all of the components from a specified entity
     *
     * @param entityId
     * @return returns the total removed
     */
    public int removeAll(long entityId) {
        int count = 0;
        for (TLongObjectMap<Component> entityMap : store.values()) {
            Component component = entityMap.remove(entityId);
            if (component != null)
                count++;
        }
        return count;
    }

    /**
     * Clears all components mappings
     */
    public void clear() {
        store.clear();
    }

    /**
     * Counts the components of the specified component type
     *
     * @param componentClass
     * @return returns the total number of components with the given type
     */
    public int getComponentCount(Class<? extends Component> componentClass) {
        TLongObjectMap<Component> map = store.get(componentClass);
        return (map == null) ? 0 : map.size();
    }

    /**
     * @return an iterable that should be only used for iteration over the components. It can't be used to remove
     * components. It should not be used after components have been added or removed from the entity.
     */
    public Iterable<Component> iterateComponents(long entityId) {
        return getComponentsInNewList(entityId);
    }


    /**
     * @return a new modifable list instance that contains all the components the entity had at the
     * time this method got called.
     */
    public List<Component> getComponentsInNewList(long entityId) {
        List<Component> components = Lists.newArrayList();
        for (TLongObjectMap<Component> componentMap : store.values()) {
            Component comp = componentMap.get(entityId);
            if (comp != null) {
                components.add(comp);
            }
        }
        return components;
    }

    /**
     * Creates an iterator for the components of the specified type
     *
     * @param componentClass the class to iterate
     * @param <T>
     * @return collection of iterable component
     */
    public <T extends Component> TLongObjectIterator<T> componentIterator(Class<T> componentClass) {
        TLongObjectMap<T> entityMap = (TLongObjectMap<T>) store.get(componentClass);
        if (entityMap != null) {
            return entityMap.iterator();
        }
        return null;
    }

    /**
     * Produces an iterator for iterating over all entities
     * <br><br>
     * This is not designed to be performant, and in general usage entities should not be iterated over.
     *
     * @return An iterator over all entity ids.
     */
    public TLongIterator entityIdIterator() {
        TLongSet idSet = new TLongHashSet();
        for (TLongObjectMap<Component> componentMap : store.values()) {
            idSet.addAll(componentMap.keys());
        }
        return idSet.iterator();
    }

    /**
     * Counts the number of entities for the table
     *
     * @return
     */
    public int numEntities() {
        TLongSet idSet = new TLongHashSet();
        for (TLongObjectMap<Component> componentMap : store.values()) {
            idSet.addAll(componentMap.keys());
        }
        return idSet.size();
    }

    public void remove(long entityId) {
        for (TLongObjectMap<Component> entityMap : store.values()) {
            entityMap.remove(entityId);
        }
    }

}
