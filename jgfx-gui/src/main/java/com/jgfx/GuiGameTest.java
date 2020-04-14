package com.jgfx;

import com.jgfx.assets.naming.Name;
import com.jgfx.assets.naming.Version;
import com.jgfx.engine.defaults.AssetLoad;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.Game;
import com.jgfx.engine.game.GameEngine;
import com.jgfx.engine.game.GameRunException;
import com.jgfx.engine.game.GameRunner;
import com.jgfx.engine.glfw.GlfwInputSubsystem;
import com.jgfx.engine.glfw.GlfwWindowSubsystem;
import com.jgfx.engine.load.LoadProcess;
import com.jgfx.gui.JgfxGui;

import java.util.List;

@Game(version = "0.0.1", name = "gui-test")
public class GuiGameTest extends GameEngine {
    public GuiGameTest(Name name, Version version, List<EngineSubsystem> subsystems, List<EntitySystem> entitySystems, List<LoadProcess> loadProcesses) {
        super(name, version, subsystems, entitySystems, loadProcesses);
    }

    /**
     * Create the systems for the gui
     */
    @Override
    protected void preInitialization() {
        //we want to be able to test textures so we must load them
        addLoadProcess(new AssetLoad());
        addSubsystem(new GlfwWindowSubsystem());
        addSubsystem(new GlfwInputSubsystem());
        addSubsystem(new JgfxGui());
    }

    public static void main(String[] args) throws GameRunException {
        GameRunner.run(GuiGameTest.class);
    }
}