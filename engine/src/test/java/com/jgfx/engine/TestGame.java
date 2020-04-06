package com.jgfx.engine;

import com.jgfx.assets.naming.Name;
import com.jgfx.assets.naming.Version;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.Game;
import com.jgfx.engine.game.GameEngine;
import com.jgfx.engine.load.LoadProcess;

import java.util.List;

@Game(name = "test-game", version = "0.0.1")
public class TestGame extends GameEngine {

    public TestGame(Name name, Version version, List<EngineSubsystem> subsystems, List<EntitySystem> entitySystems, List<LoadProcess> loadProcesses) {
        super(name, version, subsystems, entitySystems, loadProcesses);
    }

    @Override
    protected void preInitialization() {
    }

    @Override
    protected void update(float delta) {
    }

    @Override
    protected void postInitialization() {
    }
}
