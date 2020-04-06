package com.jgfx.chunk.systems;

import com.jgfx.chunk.data.ChunkBlocks;
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
import com.jgfx.utils.ShapeFactory;
import com.jgfx.utils.Side;
import com.jgfx.utils.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class will build the mesh for a given chunk
 */
@AutoRegister
public class ChunkMeshBuilder extends EntitySystem {
    private Group chunks;
    private Logger logger;
    @In private Atlas atlas;

    public ChunkMeshBuilder() {
        this.logger = LogManager.getLogger(ChunkMeshBuilder.class);
    }

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
            var state = chunk.getComponent(ChunkState.class);
            if (state.state == State.BLOCKS_LOADED || state.state == State.NEEDS_REBUILD) {
                var mesh = chunk.getComponent(ChunkMesh.class);
                var blocks = chunk.getComponent(ChunkBlocks.class);
                var origin = chunk.getComponent(ChunkOrigin.class);
                rebuildMesh(mesh, blocks, state);
                logger.debug("Chunk[{}, {}, {}] generated mesh!", origin.x, origin.y, origin.z);
            }
        });
    }

    /**
     * Rebuilds the mesh for the given chunk
     */
    private void rebuildMesh(ChunkMesh mesh, ChunkBlocks blocks, ChunkState state) {
        blocks.foreachBlock((position, block) -> {
            var sideMeta = (byte) 0;
            for (var side : Side.values()) {
                sideMeta = side.addSide(sideMeta);
            }
            block.addToChunk(position.x, position.y, position.z, sideMeta, atlas, mesh.meshData);
        });

        mesh.vao = Vao.create(2);
        mesh.vao.bind();
        mesh.vao.createAttribute(mesh.meshData.getVertices(), 3);
        mesh.vao.createAttribute(mesh.meshData.getUvs(), 2);
        mesh.vao.createIndexBuffer(mesh.meshData.getIndices());
        mesh.vao.unbind();
        state.state = State.MODEL_LOADED;//Testing for now TODO: remove this and add model loading elsewhere
    }
}
