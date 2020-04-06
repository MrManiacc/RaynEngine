package com.jgfx.chunk.events;

import com.jgfx.engine.ecs.entity.ref.EntityRef;

public class ChunkDestroyedEvent {
    public final EntityRef chunk;

    public ChunkDestroyedEvent(EntityRef chunk) {
        this.chunk = chunk;
    }
}
