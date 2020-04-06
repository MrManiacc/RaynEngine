package com.jgfx.chunk.utils;

import com.jgfx.chunk.data.*;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.utils.GroupsBuilder;

/**
 * Stores the components needs to a specific constant group
 */
public enum Groups {
    /**
     * These are all required components for a chunk.
     */
    CHUNK(
            ChunkBlocks.class, ChunkMesh.class, ChunkNeighbors.class, ChunkOrigin.class, ChunkState.class
    ),
    /**
     * Represents a block entity, which can store data, and have gui and do other things
     */
    BLOCK_ENTITY(

    );


    public final Class<? extends Component>[] value;

    Groups(Class<? extends Component>... classes) {
        this.value = classes;
    }

    /**
     * @return returns a built group using the custom {@link GroupsBuilder} class.
     */
    public Group group() {
        return new GroupsBuilder().group(this).build();
    }
}
