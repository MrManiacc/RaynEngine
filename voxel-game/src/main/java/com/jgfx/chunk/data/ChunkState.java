package com.jgfx.chunk.data;

import com.jgfx.engine.ecs.component.Component;
import com.jgfx.utils.State;

/**
 * Simply stores the current state of the chunk
 */
public class ChunkState implements Component {
    public State state;

    public ChunkState(State state) {
        this.state = state;
    }

    public ChunkState() {
        this.state = State.UNLOADED;
    }
}
