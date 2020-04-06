package com.jgfx.engine.load;

import lombok.Getter;

public abstract class SingleStepLoadProcess implements LoadProcess {
    @Getter
    private final String message;

    public SingleStepLoadProcess(String message) {
        this.message = message;
    }

    //Ignored
    public void begin() {
    }

}
