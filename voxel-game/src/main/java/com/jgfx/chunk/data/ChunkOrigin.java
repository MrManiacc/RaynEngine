package com.jgfx.chunk.data;

import com.jgfx.chunk.utils.ChunkHelper;
import com.jgfx.engine.ecs.component.Component;
import org.joml.Matrix4f;

/**
 * Represents a simple origin for a chunk
 */
public class ChunkOrigin implements Component {
    public final int x, y, z;
    public final long index;
    public final Matrix4f modelMatrix;

    public ChunkOrigin(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = ChunkHelper.getChunkIndex(x, y, z);
        this.modelMatrix = new Matrix4f().identity().translate(x, y, z).scale(1.0f);
    }
}
