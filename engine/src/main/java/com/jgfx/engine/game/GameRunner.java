package com.jgfx.engine.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jgfx.assets.naming.Name;
import com.jgfx.assets.naming.Version;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.load.LoadProcess;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class will do it's best to find all of the appropriate load processes,
 * entity systems, and sub systems
 */
public class GameRunner {
    private Class<? extends GameEngine> game;
    private List<EngineSubsystem> subsystems;
    private List<EntitySystem> entitySystems;
    private List<LoadProcess> loadProcesses;

    @Getter private Map<Class<? extends EngineSubsystem>, Integer> mappedSubsystems;
    @Getter private Map<Class<? extends EntitySystem>, Integer> mappedEntitySystems;
    @Getter private Map<Class<? extends EntitySystem>, Class[]> mappedEntitySystemsOrder;
    @Getter private Map<Class<? extends LoadProcess>, Integer> mappedLoadProcesses;
    private static final Logger logger = LogManager.getLogger();

    private GameRunner(Class<? extends GameEngine> game) throws GameRunException {
        this.game = game;
        this.mappedSubsystems = Maps.newConcurrentMap();
        this.mappedEntitySystems = Maps.newConcurrentMap();
        this.mappedLoadProcesses = Maps.newConcurrentMap();
        this.mappedEntitySystemsOrder = Maps.newConcurrentMap();
        execute();
    }

    /**
     * Executes the game
     */
    private void execute() throws GameRunException {
        if (!game.isAnnotationPresent(Game.class))
            throw new GameRunException("Error while initializing '" + game.getName() + "', no @Game annotation found!");
        var gameAnnotation = game.getAnnotation(Game.class);
        var name = new Name(gameAnnotation.name());
        var version = new Version(gameAnnotation.version());
        var constructor = getGameConstructor();
        discover();
        instantiate();
        try {
            var engine = constructor.newInstance(name, version, subsystems, entitySystems, loadProcesses);
            engine.run();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new GameRunException("Failed to initialize constructor for class '" + game.getName() + "'");
        }
    }

    /**
     * Finds all of the classes that are marked with @Subsystem, and creates a new instance
     */
    private void discover() {
        var reflections = new Reflections(game.getPackage().getName());
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(AutoRegister.class)) {
            var autoRegister = clazz.getAnnotation(AutoRegister.class);
            if (EngineSubsystem.class.isAssignableFrom(clazz)) {
                //We're processing an engine subsystem
                if (autoRegister.value() >= 0)
                    logger.debug("Found engine subsystem '" + clazz.getName() + "', with order '" + autoRegister.value() + "'.");
                else
                    logger.debug("Found engine subsystem '" + clazz.getName() + "'.");
                mappedSubsystems.put((Class<? extends EngineSubsystem>) clazz, autoRegister.value());
            } else if (EntitySystem.class.isAssignableFrom(clazz)) {
                //We're processing an engine subsystem
                if (autoRegister.value() >= 0)
                    logger.debug("Found entity system '" + clazz.getName() + "', with order '" + autoRegister.value() + "'.");
                else
                    logger.debug("Found entity system '" + clazz.getName() + "'.");

                if (autoRegister.after().length > 0) {
                    this.mappedEntitySystemsOrder.put((Class<? extends EntitySystem>) clazz, autoRegister.after());
                }
                mappedEntitySystems.put((Class<? extends EntitySystem>) clazz, autoRegister.value());
            } else if (LoadProcess.class.isAssignableFrom(clazz)) {
                //We're processing an engine subsystem
                if (autoRegister.value() >= 0)
                    logger.debug("Found load process '" + clazz.getName() + "', with order '" + autoRegister.value() + "'.");
                else
                    logger.debug("Found load process '" + clazz.getName() + "'.");
                mappedLoadProcesses.put((Class<? extends LoadProcess>) clazz, autoRegister.value());
            }
        }
        this.subsystems = Lists.newArrayListWithCapacity(mappedSubsystems.size());
        this.entitySystems = Lists.newArrayListWithCapacity(mappedEntitySystems.size());
        this.loadProcesses = Lists.newArrayListWithCapacity(mappedLoadProcesses.size());
    }

    /**
     * This class will attempt to instantiate the mapped entity systems and subsystems
     */
    private void instantiate() throws GameRunException {
        for (var subsystem : mappedSubsystems.keySet()) {
            var order = mappedSubsystems.get(subsystem);
            try {
                var constructor = subsystem.getConstructor();
                if (order >= 0)
                    subsystems.add(order, constructor.newInstance());
                else
                    subsystems.add(constructor.newInstance());
                logger.debug("Successfully instantiated engine subsystem '" + subsystem.getName() + "'.");
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new GameRunException("Error while instantiating '" + subsystem.getName() + "', you must provide a zero argument constructor!");
            }
        }

        for (var entitySystem : mappedEntitySystems.keySet()) {
            try {
                var constructor = entitySystem.getConstructor();
                var instance = constructor.newInstance();
                if (mappedEntitySystemsOrder.containsKey(entitySystem))
                    instance.setLoadAfter(mappedEntitySystemsOrder.get(entitySystem));
                entitySystems.add(instance);
                Collections.sort(entitySystems);
                logger.debug("Successfully instantiated entity system '" + entitySystem.getName() + "'.");
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new GameRunException("Error while instantiating '" + entitySystem.getName() + "', you must provide a zero argument constructor!");
            }
        }


        for (var loadProcess : mappedLoadProcesses.keySet()) {
            var order = mappedLoadProcesses.get(loadProcess);
            try {
                var constructor = loadProcess.getConstructor();
                if (order >= 0)
                    loadProcesses.add(order, constructor.newInstance());
                else
                    loadProcesses.add(constructor.newInstance());
                logger.debug("Successfully instantiated load processes '" + loadProcess.getName() + "'.");
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new GameRunException("Error while instantiating '" + loadProcess.getName() + "', you must provide a zero argument constructor!");
            }
        }
    }


    /**
     * @return returns the proper constructor or null, can throw an exception if it's not found
     */
    private Constructor<? extends GameEngine> getGameConstructor() throws GameRunException {
        try {
            return game.getConstructor(Name.class, Version.class, List.class, List.class, List.class);
        } catch (NoSuchMethodException e) {
            throw new GameRunException("Error while initializing '" + game.getName() + "', you must provide a constructor with a Name, and Version!");
        }
    }


    /**
     * Runs the game
     */
    public static GameRunner run(Class<? extends GameEngine> game) throws GameRunException {
        return new GameRunner(game);
    }
}
