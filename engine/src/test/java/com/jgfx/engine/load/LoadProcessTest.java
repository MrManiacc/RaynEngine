package com.jgfx.engine.load;

import com.jgfx.engine.game.AutoRegister;

@AutoRegister
public class LoadProcessTest extends SingleStepLoadProcess {
    public LoadProcessTest() {
        super("Testing");
    }

    @Override
    public boolean step() {
        return true;
    }
}
