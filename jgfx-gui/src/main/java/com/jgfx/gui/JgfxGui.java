package com.jgfx.gui;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.ecs.World;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.event.Bus;
import com.jgfx.engine.game.GameEngine;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.gui.systems.GuiRenderer;
import lombok.Getter;

/**
 * The main entry for the gui
 */
public class JgfxGui implements EngineSubsystem {
    private boolean loaded;
    @In private World world;

    /**
     * We want to registers to all GUI events manually
     */
    @Override
    public void preInitialise() {
        Bus.GUI.register(this);
        CoreContext.put(this);
        loaded = true;
    }

    @Override
    public void initialise() {
        world.addSystem(new GuiRenderer());
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public String getName() {
        return "JgfxGui";
    }
}
