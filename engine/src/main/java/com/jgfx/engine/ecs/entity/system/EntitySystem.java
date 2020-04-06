package com.jgfx.engine.ecs.entity.system;

import com.jgfx.engine.ecs.World;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.time.GenericTime;
import lombok.Getter;
import lombok.Setter;

public abstract class EntitySystem implements Comparable<EntitySystem> {
    private int priority;
    private static int nextPriority = 0;

    @In protected EngineTime time;
    @In protected World world;
    @Setter private boolean processing = true;
    @Getter private boolean initialized = false;

    public EntitySystem() {
        this.priority = nextPriority++;
    }

    /**
     * When the entity system is initialized
     */
    public void initialize() {
    }

    /**
     * Internal processing so we can pass the time, which is nice
     *
     * @param time
     */
    protected void process(EngineTime time) {
    }

    /**
     * Post processes the entity system
     */
    protected void postProcess() {
    }

    /**
     * Updates the entity system if it's processing
     */
    public void update() {
        if (processing)
            process(time);
    }

    @Override
    public int compareTo(EntitySystem o) {
        return priority - o.priority;
    }
}
