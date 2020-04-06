package com.jgfx.chunk.data;

import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.utils.MeshData;

/**
 * Represents a chunk's mesh
 */
public class ChunkMesh implements Component {
    public final MeshData meshData;
    public Vao vao;

    public ChunkMesh() {
        this.meshData = new MeshData();
    }
}
