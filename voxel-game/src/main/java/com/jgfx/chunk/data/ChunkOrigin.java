package com.jgfx.chunk.data;

import com.jgfx.chunk.utils.ChunkHelper;
import com.jgfx.engine.ecs.component.Component;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    /**
     * @return returns the distance from the given position
     */
    public float distance(Vector3f position) {
        return distance(position.x, position.y, position.z);
    }

    /**
     * @return returns the distance from the given position
     */
    public float distance(float x, float y, float z) {
        var dx = (x - this.x) * (x - this.x);
        var dy = (y - this.y) * (y - this.y);
        var dz = (z - this.z) * (z - this.z);
        return (float) Math.sqrt(dx + dy + dz);
    }

    @Override
    public String toString() {
        return "x=" + x +
                ", y=" + y +
                ", z=" + z;
    }
}
