package com.jgfx;

import com.jgfx.assets.naming.Name;
import com.jgfx.assets.naming.Version;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.Game;
import com.jgfx.engine.game.GameEngine;
import com.jgfx.engine.game.GameRunException;
import com.jgfx.engine.game.GameRunner;
import com.jgfx.engine.glfw.GlfwInputSubsystem;
import com.jgfx.engine.glfw.GlfwWindowSubsystem;
import com.jgfx.engine.load.LoadProcess;
import com.jgfx.engine.physics.Physics2dSubsystem;

import java.util.List;

@Game(name = "pong", version = "0.0.1")
public class PongGame extends GameEngine {
    public PongGame(Name name, Version version, List<EngineSubsystem> subsystems, List<EntitySystem> entitySystems, List<LoadProcess> loadProcesses) {
        super(name, version, subsystems, entitySystems, loadProcesses);
    }

    /**
     * Here we want to add our default window subsystem
     */
    @Override
    protected void preInitialization() {
        addSubsystem(new GlfwWindowSubsystem());
        addSubsystem(new GlfwInputSubsystem());
        addSubsystem(new Physics2dSubsystem());
    }

    public static void main(String[] args) throws GameRunException {
        GameRunner.run(PongGame.class);
    }
}
