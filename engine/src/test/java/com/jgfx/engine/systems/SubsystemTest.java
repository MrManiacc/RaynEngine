package com.jgfx.engine.systems;

import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.game.AutoRegister;

@AutoRegister
public class SubsystemTest implements EngineSubsystem {

    @Override
    public String getName() {
        return "Test-subsystem";
    }

    @Override
    public boolean isLoaded() {
        return false;
    }
}
