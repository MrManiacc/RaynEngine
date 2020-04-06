package com.jgfx.engine.ecs.entity.pool;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.component.ComponentTable;
import com.jgfx.engine.ecs.component.SingleComponent;
import com.jgfx.engine.ecs.entity.builder.EntityBuilder;
import com.jgfx.engine.ecs.entity.ref.BaseEntityRef;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.util.EntityIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The core entity manager. This will handle all of the entities in the entire game
 */
public class EntityManager implements EntityPool {
    //********Data about entities initialization
    @Setter
    @Getter
    private long nextId = 1;
    private TLongSet loadedIds = new TLongHashSet();

    //********Data about entities and components
    @Getter private Map<Long, BaseEntityRef> entityStore = new MapMaker().weakValues().concurrencyLevel(4).initialCapacity(1000).makeMap();
    @Getter private ComponentTable componentStore = new ComponentTable();

    @Getter private final Iterable<EntityRef> allEntities;
    private Map<ResourceUrn, EntityRef> singleEntityStore = Maps.newConcurrentMap();

    /**
     * Creates the all entities iterator
     */
    public EntityManager() {
        allEntities = () -> new EntityIterator(componentStore.entityIdIterator(), this);
    }

    /**
     * Removes all entities from the pool.
     */
    @Override
    public void clear() {
        for (EntityRef entity : entityStore.values()) {
            entity.dispose();
        }
        componentStore.clear();
        entityStore.clear();
    }

    /**
     * Create a new entity builder with this as a reference
     *
     * @return returns the entity builder
     */
    @Override
    public EntityBuilder newBuilder() {
        return new EntityBuilder(this);
    }

    @Override
    public EntityRef create() {
        var builder = newBuilder();
        return builder.build();
    }

    @Override
    public EntityRef create(Component... components) {
        var builder = newBuilder();
        for (var component : components)
            builder.addComponent(component);
        return builder.build();
    }

    @Override
    public EntityRef create(Iterable<Component> components) {
        var builder = newBuilder();
        builder.addComponents(components);
        return builder.build();
    }

    @Override
    public EntityRef getEntity(long id) {
        if (id == -1 || !isExistingEntity(id)) {
            // ID is null or the entity doesn't exist
            return EntityRef.NULL;
        }
        var existing = entityStore.get(id);
        if (existing != null) {
            // Entity already has a ref
            return existing;
        }
        return EntityRef.NULL;
    }

    /**
     * Puts an entity into the internal storage.
     * <p>
     * This is intended for use by the
     * In most cases, it is better to use the {@link #create} or {@link #newBuilder} methods instead.
     *
     * @param entityId the id of the entity to add
     * @param ref      the {@link BaseEntityRef} to add
     */
    @Override
    public void putEntity(long entityId, BaseEntityRef ref) {
        entityStore.put(entityId, ref);
    }

    public Iterable<EntityRef> getEntitiesWithAllAndExclude(Class<? extends Component>[] allClasses, Class<? extends Component>[] excludeClasses) {
        return () -> entityStore.keySet().stream()
                //Keep entities which have all of the required components
                .filter(id -> Arrays.stream(allClasses)
                        .allMatch(component -> componentStore.get(id, component) != null))
                .filter(id -> Arrays.stream(excludeClasses)
                        .noneMatch(comp -> componentStore.get(id, comp) != null))
                .map(this::getEntity)
                .iterator();
    }

    public Iterable<EntityRef> getEntitiesWithOneAndExclude(Class<? extends Component>[] oneClasses, Class<? extends Component>[] excludeClasses) {
        return () -> entityStore.keySet().stream()
                //Keep entities which have all of the required components
                .filter(id -> Arrays.stream(oneClasses)
                        .anyMatch(component -> componentStore.get(id, component) != null))
                .filter(id -> Arrays.stream(excludeClasses)
                        .noneMatch(comp -> componentStore.get(id, comp) != null))
                .map(this::getEntity)
                .iterator();
    }

    public Iterable<EntityRef> getEntitiesWithOneOrAllAndExclude(Class<? extends Component>[] oneClasses, Class<? extends Component>[] allClasses, Class<? extends Component>[] excludeClasses) {
        return () -> entityStore.keySet().stream()
                //Keep entities which have all of the required components
                .filter(id -> Arrays.stream(oneClasses)
                        .anyMatch(component -> componentStore.get(id, component) != null))
                .filter(id -> Arrays.stream(allClasses)
                        .allMatch(component -> componentStore.get(id, component) != null))
                .filter(id -> Arrays.stream(excludeClasses)
                        .noneMatch(comp -> componentStore.get(id, comp) != null))
                .map(this::getEntity)
                .iterator();
    }

    /**
     * Gets all of the specified entities that math the given classes
     *
     * @param componentClasses
     * @return
     */
    @Override
    public Iterable<EntityRef> getEntitiesWithAll(Class<? extends Component>... componentClasses) {
        return () -> entityStore.keySet().stream()
                //Keep entities which have all of the required components
                .filter(id -> Arrays.stream(componentClasses)
                        .allMatch(component -> componentStore.get(id, component) != null))
                .map(this::getEntity)
                .iterator();
    }

    /**
     * Gets all of the specified entities that math the given classes
     *
     * @param componentClasses
     * @return
     */
    @Override
    public Iterable<EntityRef> getEntitiesWithOne(Class<? extends Component>... componentClasses) {
        return () -> entityStore.keySet().stream()
                //Keep entities which have all of the required components
                .filter(id -> Arrays.stream(componentClasses)
                        .anyMatch(component -> componentStore.get(id, component) != null))
                .map(this::getEntity)
                .iterator();
    }

    /**
     * Gets all of the specified entities that math the given classes
     *
     * @return returns entities with all of the all types, and one of the one types
     */
    @Override
    public Iterable<EntityRef> getEntitiesWithOneOrAll(Class<? extends Component>[] oneComponentClasses, Class<? extends Component>[] allComponentClasses) {
        return () -> entityStore.keySet().stream()
                //Keep entities which have all of the required components
                .filter(id -> Arrays.stream(oneComponentClasses)
                        .anyMatch(component -> componentStore.get(id, component) != null))
                .filter(id -> Arrays.stream(allComponentClasses)
                        .allMatch(component -> componentStore.get(id, component) != null))
                .map(this::getEntity)
                .iterator();
    }

    /**
     * Counts the number of entities that have the given components. This is a slower method
     *
     * @param componentClasses
     * @return
     */
    @Override
    public int getCountOfEntitiesWith(Class<? extends Component>... componentClasses) {
        return Lists.newArrayList(getEntitiesWithAll(componentClasses)).size();
    }

    /**
     * Gets the total number of active entities
     *
     * @return
     */
    @Override
    public int getActiveEntityCount() {
        return entityStore.size();
    }

    /**
     * Checks to see if the entity store has the entity stored
     *
     * @param id the id of the entity to search for
     * @return
     */
    @Override
    public boolean contains(long id) {
        return entityStore.containsKey(id);
    }


    /**
     * Destorys an entity removes it's components and references
     *
     * @param id
     */
    @Override
    public void destroy(long id) {
        // Don't allow the destruction of unloaded entities.
        if (!isIdLoaded(id))
            return;
        EntityRef ref = getEntity(id);
        long entityId = ref.getId();
        entityStore.remove(entityId);
        loadedIds.remove(entityId);
        ref.dispose();
        componentStore.remove(entityId);
    }

    /**
     * Checks to see if the entity is valid
     *
     * @param id
     * @return returns true if valid
     */
    private boolean isExistingEntity(long id) {
        return nextId > id;
    }

    @Override
    public boolean hasComponent(long entityId, Class<? extends Component> componentClass) {
        return false;
    }

    @Override
    public Optional<BaseEntityRef> remove(long id) {
        componentStore.remove(id);
        return Optional.of(entityStore.remove(id));
    }

    /**
     * Inserts a  reference to the store
     *
     * @param ref        the EntityRef of the entity to be inserted
     * @param components the entity's components
     */
    @Override
    public void insertRef(BaseEntityRef ref, Iterable<Component> components) {
        var insert = new AtomicBoolean(true);
        components.forEach(comp -> {
            if (comp instanceof SingleComponent) {
                var singleEntity = (SingleComponent) comp;
                if (!singleEntityStore.containsKey(singleEntity.urn))
                    singleEntityStore.put(singleEntity.urn, ref);
                else
                    insert.set(false);
            }
        });
        if (insert.get()) {
            components.forEach(comp -> componentStore.put(ref.getId(), comp));
            entityStore.put(ref.getId(), ref);
        }
    }

    /**
     * Gets a single entity resource which is stored upon insertion
     *
     * @param urn the urn of the single entity
     * @return returns the entity reference
     */
    public EntityRef getSingleEntity(ResourceUrn urn) {
        return singleEntityStore.get(urn);
    }

    /**
     * Gets the next entity id
     *
     * @return
     */
    @Override
    public long createEntity() {
        if (nextId == -1) {
            nextId++;
        }
        loadedIds.add(nextId);
        return nextId++;
    }

    @Override
    public boolean registerId(long id) {
        if (id >= nextId) {
            return false;
        }
        loadedIds.add(id);
        return true;
    }

    /**
     * Adds (or replaces) a component to an entity
     *
     * @param entityId
     * @param component
     * @param <T>
     * @return
     */
    @Override
    public <T extends Component> T addComponent(long entityId, T component) {
        Preconditions.checkNotNull(component);
        componentStore.put(entityId, component);
        return component;
    }

    /**
     * @param id
     * @param componentClass
     * @param <T>
     * @return The component of that type owned by the given entity, or null if it doesn't have that component
     */
    public <T extends Component> T getComponent(long id, Class<T> componentClass) {
        return componentStore.get(id, componentClass);
    }

    /**
     * Removes a component from an entity
     *
     * @param entityId
     * @param componentClass
     */
    @Override
    public <T extends Component> T removeComponent(long entityId, Class<T> componentClass) {
        var component = componentStore.get(entityId, componentClass);
        if (component != null)
            componentStore.remove(entityId, componentClass);
        return component;
    }

    /**
     * Lists the components of a given component type, with a ref to their entity
     *
     * @param componentClass
     * @param <T>
     * @return
     */
    public <T extends Component> Iterable<Map.Entry<EntityRef, T>> listComponents(Class<T> componentClass) {
        TLongObjectIterator<T> iterator = componentStore.componentIterator(componentClass);
        List<Map.Entry<EntityRef, T>> list = new ArrayList<>();
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.advance();
                list.add(new EntityEntry<>(getEntity(iterator.key()), iterator.value()));
            }
        }
        return list;
    }

    /**
     * Checks to see if the id is loaded
     *
     * @param entityId
     * @return
     */
    public boolean isIdLoaded(long entityId) {
        return loadedIds.contains(entityId);
    }


    /**
     * Creates a copy of the components of an entity.
     *
     * @return A map of components types to components copied from the target entity.
     */
    public Map<Class<? extends Component>, Component> copyComponents(EntityRef other) {
        Map<Class<? extends Component>, Component> result = Maps.newHashMap();
        for (Component c : other.iterateComponents()) {
            result.put(c.getClass(), c);
        }
        return result;
    }


    private static class EntityEntry<T> implements Map.Entry<EntityRef, T> {
        private EntityRef key;
        private T value;

        EntityEntry(EntityRef ref, T value) {
            this.key = ref;
            this.value = value;
        }

        @Override
        public EntityRef getKey() {
            return key;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public T setValue(T newValue) {
            throw new UnsupportedOperationException();
        }
    }


}
