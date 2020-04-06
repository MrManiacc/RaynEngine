package com.jgfx.chunk.events;

import com.jgfx.engine.ecs.entity.ref.EntityRef;

/**
 * Called when a chunk is generated
 */
public class ChunkGeneratedEvent {
    public final EntityRef chunk;

    public ChunkGeneratedEvent(EntityRef chunk) {
        this.chunk = chunk;
    }
}
