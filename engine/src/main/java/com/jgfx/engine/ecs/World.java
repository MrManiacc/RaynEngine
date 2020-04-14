package com.jgfx.engine.ecs;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.entity.pool.EntityManager;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.entity.system.EntitySystemManager;
import lombok.Getter;

/**
 * Combines all of the aspects of the ecs stuff, into one world class
 */
public class World {
    @Getter private EntityManager entityManager;
    @Getter private EntitySystemManager systemManager;

    public World() {
        this.entityManager = CoreContext.put(new EntityManager());
        this.systemManager = CoreContext.put(new EntitySystemManager());
    }

    /**
     * Adds an entity system
     */
    public void addSystem(EntitySystem system) {
        systemManager.add(system);
    }

    /**
     * Removes an entitiy system
     */
    public <T extends EntitySystem> T removeSystem(Class<T> system) {
        return systemManager.remove(system);
    }

    /**
     * @return returns the entity system with the specified class
     */
    public <T extends EntitySystem> T getSystem(Class<T> system) {
        return systemManager.get(system);
    }

    /**
     * Processes the world
     */
    public void process() {
        systemManager.process();
    }

    /**
     * Processes the world after the update
     */
    public void postProcess() {
        systemManager.postProcess();
    }

    /**
     * Initializes the world AFTER all of the systems have been added
     */
    public void initialize() {
        systemManager.initialize();
    }

    /**
     * Called after everything has been initialized
     */
    public void postInitialize() {
        systemManager.postInitialize();
    }

    /**
     * @return returns an entity with no components
     */
    public EntityRef createEntity() {
        return entityManager.create();
    }

    /**
     * @return returns and entity with the specified components
     */
    public EntityRef createEntity(Component... components) {
        return entityManager.create(components);
    }

    /**
     * @return returns and entity with the specified components
     */
    public EntityRef createEntity(Iterable<Component> components) {
        return entityManager.create(components);
    }

    /**
     * @return returns an entity with the specified id
     */
    public EntityRef getEntity(long id) {
        return entityManager.getEntity(id);
    }

    /**
     * @return returns true if the entity with the id is present
     */
    public boolean hasEntity(long id) {
        return entityManager.contains(id);
    }

    /**
     * Deletes the entity with the specified id
     */
    public void deleteEntity(long id) {
        entityManager.destroy(id);
    }

    /**
     * Dispose of the world
     */
    public void dispose() {
        entityManager.clear();
    }
}
