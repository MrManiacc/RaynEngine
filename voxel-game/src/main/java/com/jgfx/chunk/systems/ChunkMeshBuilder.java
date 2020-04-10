package com.jgfx.chunk.systems;

import com.jgfx.chunk.data.ChunkMesh;
import com.jgfx.chunk.data.ChunkOrigin;
import com.jgfx.chunk.data.ChunkState;
import com.jgfx.chunk.utils.Groups;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.tiles.atlas.Atlas;
import com.jgfx.utils.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class will build the mesh for a given chunk
 */
@AutoRegister
public class ChunkMeshBuilder extends EntitySystem {
    private Group chunks;
    private static final Logger logger = LogManager.getLogger(ChunkMeshBuilder.class);
    @In private Atlas atlas;

    /**
     * Initialize our chunks
     */
    @Override
    public void initialize() {
        chunks = Groups.CHUNK.group();
    }

    /**
     * Here we're checking for blocks that have either {@link State.UNLOADED} or {@link State.NEEDS_REBUILD}
     */
    @Override
    protected void process(EngineTime time) {
        chunks.forEach(chunk -> {
            var state = chunk.get(ChunkState.class);
            if (state.state == State.MESH_LOADED ) {
                var mesh = chunk.get(ChunkMesh.class);
                var origin = chunk.get(ChunkOrigin.class);
                rebuildMesh(mesh, state);
                logger.debug("Chunk[{}, {}, {}] generated model!", origin.x, origin.y, origin.z);
            }
        });
    }

    /**
     * Rebuilds the mesh for the given chunk
     */
    private void rebuildMesh(ChunkMesh mesh, ChunkState state) {
        mesh.vao = Vao.create(2);
        mesh.vao.bind();
        mesh.vao.createAttribute(mesh.meshData.getVertices(), 3);
        mesh.vao.createAttribute(mesh.meshData.getUvs(), 2);
        mesh.vao.createIndexBuffer(mesh.meshData.getIndices());
        mesh.vao.unbind();
        state.state = State.MODEL_LOADED;//Testing for now TODO: remove this and add model loading elsewhere
    }
}
