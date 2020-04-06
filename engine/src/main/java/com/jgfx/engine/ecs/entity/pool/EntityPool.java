package com.jgfx.engine.ecs.entity.pool;


import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.component.ComponentTable;
import com.jgfx.engine.ecs.entity.builder.EntityBuilder;
import com.jgfx.engine.ecs.entity.ref.BaseEntityRef;
import com.jgfx.engine.ecs.entity.ref.EntityRef;

import java.util.Map;
import java.util.Optional;

/**
 * A pool is defined as a group of entities that share one or more components (generally)
 */
public interface EntityPool {
    /**
     * Removes all entities from the pool.
     */
    void clear();

    /**
     * Creates an EntityBuilder.
     *
     * @return A new entity builder
     */
    EntityBuilder newBuilder();

    /**
     * @return A references to a new, unused entity
     */
    EntityRef create();

    /**
     * @return A references to a new, unused entity with the desired components
     */
    EntityRef create(Component... components);

    /**
     * @return A references to a new, unused entity with the desired components
     */
    EntityRef create(Iterable<Component> components);

    /**
     * Retrieve the entity ref with the given id.
     *
     * @return the {@link EntityRef}, if it exists; {@link EntityRef#NULL} otherwise
     */
    EntityRef getEntity(long id);

    /**
     * Puts an entity into the internal storage.
     * <p>
     * This is intended for use by the {@link PojoEntityManager}.
     * In most cases, it is better to use the {@link #create} or {@link #newBuilder} methods instead.
     *
     * @param entityId the id of the entity to add
     * @param ref      the {@link BaseEntityRef} to add
     */
    void putEntity(long entityId, BaseEntityRef ref);

    /**
     * @return an iterable over all of the entities in this pool
     */
    Iterable<EntityRef> getAllEntities();

    /**
     * @return An iterable over all entities with the provided component types.
     */
    Iterable<EntityRef> getEntitiesWithAll(Class<? extends Component>... componentClasses);

    /**
     * @return An iterable over all entities with the provided component types.
     */
    Iterable<EntityRef> getEntitiesWithOne(Class<? extends Component>... componentClasses);

    /**
     * @return An iterable over all entities with the provided component types.
     */
    Iterable<EntityRef> getEntitiesWithOneOrAll(Class<? extends Component>[] oneComponentClasses, Class<? extends Component>[] allComponentClasses);

    /**
     * @return A count of entities with the provided component types
     */
    int getCountOfEntitiesWith(Class<? extends Component>... componentClasses);

    /**
     * @return A count of currently active entities
     */
    int getActiveEntityCount();

    /**
     * Does this pool contain the given entity?
     *
     * @param id the id of the entity to search for
     * @return true if this pool contains the entity; false otherwise
     */
    boolean contains(long id);

    /**
     * This is used to persist the entity manager's state
     *
     * @return The id that will be used for the next entity (after freed ids are used)
     */
    long getNextId();

    /**
     * Sets the next id the entity manager will use. This is used when restoring the entity manager's state.
     *
     * @param id
     */
    void setNextId(long id);

    ComponentTable getComponentStore();

    /**
     * Destroys an entity with the given id
     *
     * @param id
     */
    void destroy(long id);

    /**
     * Fund out if a particular entity has a component of the given class.
     *
     * @param entityId       the entity to check
     * @param componentClass the class to check for
     * @return whether the entity has the component
     */
    boolean hasComponent(long entityId, Class<? extends Component> componentClass);


    /**
     * Remove the entity from the pool. This does not destroy the entity, it only removes the {@link BaseEntityRef}
     * and the {@link Component}s from this pool, so that the entity can be moved to a different pool. It does
     * not invalidate the {@link EntityRef}.
     * <p>
     * Returns an {@link Optional} {@link BaseEntityRef} if it was removed, ready to be put into another pool. If
     * nothing was removed, return {@link Optional#empty()}.
     * <p>
     * This method is intended for use by. Caution
     * should be taken if this method is used elsewhere, and the caller should ensure that the the entity is
     * immediately placed in a new pool. All of the components are removed, so should be manually copied before this
     * method is called if they are to be kept in use.
     *
     * @param id the id of the entity to remove
     * @return an optional {@link BaseEntityRef}, containing the removed entity
     */
    Optional<BaseEntityRef> remove(long id);

    /**
     * Creates a copy of the components of an entity.
     *
     * @return A map of components types to components copied from the target entity.
     */
    Map<Class<? extends Component>, Component> copyComponents(EntityRef other);

    /**
     * Insert the {@link BaseEntityRef} into this pool, with these {@link Component}s. This only inserts the ref, adds the
     * components, and assigns the entity to this pool in the EntityManager.
     * <p>
     * No events are sent, so this should only be used when inserting a ref that has been created elsewhere. It is
     * intended for use by, so caution should be taken
     * if this method is used elsewhere.
     *
     * @param ref        the EntityRef of the entity to be inserted
     * @param components the entity's components
     */
    void insertRef(BaseEntityRef ref, Iterable<Component> components);

    /**
     * Gets a single entity instance from the resource urn
     *
     * @param urn the urn to get entity for
     * @return returns the entity reference
     */
    EntityRef getSingleEntity(ResourceUrn urn);

    /**
     * Creates a new entity.
     * <p>
     * This method is designed for internal use by the EntityBuilder; the {@link #create} methods should be used in
     * most circumstances.
     *
     * @return the id of the newly created entity
     */
    long createEntity();

    /**
     * Attempts to register a new id with the entity manager.
     * <p>
     * This method is designed for internal use by the EntityBuilder.
     *
     * @param id the id to register
     * @return whether the registration was successful
     */
    boolean registerId(long id);

    /**
     * Adds (or replaces) a component to an entity
     *
     * @param entityId
     * @param component
     * @param <T>
     * @return The added component
     */
    <T extends Component> T addComponent(long entityId, T component);

    /**
     * Removes a component from an entity
     *
     * @param entityId
     * @param componentClass
     */
    <T extends Component> T removeComponent(long entityId, Class<T> componentClass);

    /**
     * @param id
     * @param componentClass
     * @param <T>
     * @return The component of that type owned by the given entity, or null if it doesn't have that component
     */
    <T extends Component> T getComponent(long id, Class<T> componentClass);

}
