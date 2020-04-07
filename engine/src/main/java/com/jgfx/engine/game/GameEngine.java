package com.jgfx.engine.game;

import com.google.common.collect.Queues;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.naming.Name;
import com.jgfx.assets.naming.Version;
import com.jgfx.engine.defaults.AssetLoad;
import com.jgfx.engine.defaults.DefaultShapesLoad;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.ecs.World;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.event.Bus;
import com.jgfx.engine.injection.Injector;
import com.jgfx.engine.load.LoadProcess;
import com.jgfx.engine.status.EngineStatus;
import com.jgfx.engine.status.EngineStatusUpdatedEvent;
import com.jgfx.engine.status.StandardGameStatus;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.time.GenericTime;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Queue;

/**
 * This is the default entry for the game engine
 */
public class GameEngine {
    @Getter private Name name;
    @Getter private Version version;
    @Getter private EngineStatus status = StandardGameStatus.UNSTARTED;
    @Getter protected EngineTime time;
    @Getter private volatile boolean running;
    private volatile boolean shutdownRequested;
    @Getter private World world;
    //========================================
    private List<EngineSubsystem> subsystems;
    private Queue<LoadProcess> loadProcesses;
    private final Logger logger = LogManager.getLogger();

    public GameEngine(Name name, Version version, List<EngineSubsystem> subsystems, List<EntitySystem> entitySystems, List<LoadProcess> loadProcesses) {
        CoreContext.put(GameEngine.class, this);
        this.name = name;
        this.version = version;
        this.world = CoreContext.put(new World());
        this.subsystems = subsystems;
        this.time = CoreContext.put(EngineTime.class, new GenericTime());
        this.loadProcesses = Queues.newArrayDeque();
        //We want to always do asset loading first
        this.loadProcesses.add(new AssetLoad());
        this.loadProcesses.add(new DefaultShapesLoad());
        this.loadProcesses.addAll(loadProcesses);
        //Register the entity systems
        entitySystems.forEach(world::addSystem);
    }

    /**
     * Starts the engine
     */
    public void run() {
        running = true;
        initialize();
        while (tick()) {
        }
    }

    /**
     * Adds a subsystem, usually for adding default systems like {@link com.jgfx.engine.glfw.GlfwWindowSubsystem}.
     * Otherwise you'll use the {@link AutoRegister} class
     */
    protected void addSubsystem(EngineSubsystem subsystem) {
        subsystems.add(subsystem);
    }

    /**
     * Initialize everything
     */
    private void initialize() {
        preInitialization();

        preInitSubsystems();

        //Load processes after subsystems initialization
        initializeLoadProcesses();

        injectSubsystems();

        initSubsystems();

        initEntitySystems();

        postInitSubsystems();

        postInitialization();
    }

    /**
     * Adds a loads process
     */
    protected void addLoadProcess(LoadProcess process) {
        loadProcesses.add(process);
    }

    /**
     * Initializes the next load process
     */
    private void initializeLoadProcesses() {
        while (!loadProcesses.isEmpty()) {
            var next = loadProcesses.peek();
            if (next != null) {
                Injector.ALL.inject(next, true);
                changeStatus(() -> "Beginning load process: '" + next.getMessage() + "'.");
                next.begin();
                while (!next.step()) {
                }
                loadProcesses.remove();
            }
        }
    }

    /**
     * Gives a chance to subsystems to do something BEFORE managers and Time are initialized.
     */
    private void preInitSubsystems() {
        changeStatus(StandardGameStatus.PREPARING_SUBSYSTEMS);
        for (EngineSubsystem subsystem : subsystems) {
            changeStatus(() -> "Pre-initialising " + subsystem.getName() + " subsystem");
            subsystem.preInitialise();
            CoreContext.put(subsystem);
        }
    }

    /**
     * Gives a chance to subsystems to do something BEFORE managers and Time are initialized.
     */
    private void initSubsystems() {
        for (EngineSubsystem subsystem : subsystems) {
            changeStatus(() -> "Initialising " + subsystem.getName() + " subsystem");
            Injector.ALL.inject(subsystem);
            subsystem.initialise();
        }
    }

    /**
     * Gives a chance to subsystems to do something BEFORE managers and Time are initialized.
     */
    private void postInitSubsystems() {
        for (EngineSubsystem subsystem : subsystems) {
            changeStatus(() -> "Post-initialising " + subsystem.getName() + " subsystem");
            Injector.ALL.inject(subsystem);
            subsystem.postInitialise();
        }
    }


    /**
     * This class will inject all of the variables inside the root context
     */
    private void injectSubsystems() {
        changeStatus(StandardGameStatus.INJECTING_INSTANCES);
        for (EngineSubsystem subsystem : subsystems) {
            changeStatus(() -> "Scanning for shared variables in " + subsystem.getName() + " subsystem");
            Injector.GENERICS.inject(subsystem, true);
        }
    }

    /**
     * Initializes all of the entity systems
     */
    public void initEntitySystems() {
        changeStatus(StandardGameStatus.INITIALIZING_ENTITY_SYSTEMS);
        world.initialize();
    }

    /**
     * Step the engine a single time
     *
     * @return returns true if the engine is stepping
     */
    private boolean tick() {
        if (shutdownRequested)
            return false;
        update();
        return true;
    }

    /**
     * Updates the engine
     */
    private void update() {
        var updateCycles = time.tick();
        for (var subsystem : subsystems)
            subsystem.preUpdate();

        world.process();

        while (updateCycles.hasNext()) {
            float updateDelta = updateCycles.next(); // gameTime gets updated here!
            update(updateDelta);
        }

        world.postProcess();

        for (EngineSubsystem subsystem : subsystems)
            subsystem.postUpdate();
    }

    /**
     * Updates the status and posts the event for the update
     *
     * @param newStatus the new status
     */
    private void changeStatus(EngineStatus newStatus) {
        logger.info("Status -> " + newStatus.getDescription());
        Bus.LOGIC.post(new EngineStatusUpdatedEvent(this.status, newStatus));
        status = newStatus;
    }


    /**
     * This can be overridden by the child engine
     */
    protected void update(float delta) {
    }

    /**
     * called before anything else, can be used in the child class
     */
    protected void preInitialization() {
    }

    /**
     * called after everything else, can be used in the child class
     */
    protected void postInitialization() {
    }

    /**
     * Shuts down the engine
     */
    public void shutdown() {
        shutdownRequested = true;
        world.dispose();
    }
}