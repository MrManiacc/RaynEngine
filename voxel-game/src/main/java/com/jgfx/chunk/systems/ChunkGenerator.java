package com.jgfx.chunk.systems;

import com.jgfx.chunk.data.ChunkBlocks;
import com.jgfx.chunk.data.ChunkMesh;
import com.jgfx.chunk.data.ChunkOrigin;
import com.jgfx.chunk.data.ChunkState;
import com.jgfx.chunk.utils.ChunkHelper;
import com.jgfx.chunk.utils.Groups;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.EventSubscriber;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.tiles.atlas.Atlas;
import com.jgfx.utils.Side;
import com.jgfx.utils.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class will generate the blocks for the chunk
 */
@EventSubscriber
@AutoRegister
public class ChunkGenerator extends EntitySystem {
    private Group chunks;
    private final Logger logger;
    private ExecutorService generationExecutor = Executors.newFixedThreadPool(4);
    @In private Atlas atlas;

    public ChunkGenerator() {
        this.logger = LogManager.getLogger(ChunkGenerator.class);
    }

    /**
     * Here we init our chunk group
     */
    @Override
    public void initialize() {
        this.chunks = Groups.CHUNK.group();
    }

    /**
     * Here we're going to generate the blocks for a chunk that isn't generated
     */
    @Override
    protected void process(EngineTime time) {
        chunks.forEach(chunk -> {
            var state = chunk.get(ChunkState.class);
            var origin = chunk.get(ChunkOrigin.class);
            var mesh = chunk.get(ChunkMesh.class);
            var blocks = chunk.get(ChunkBlocks.class);
            if (state.state == State.UNLOADED || state.state == State.NEEDS_REBUILD) {
                generateChunk(blocks, state, origin);
            }
            if (state.state == State.BLOCKS_LOADED) {
                generationExecutor.submit(generateMesh(blocks, mesh, state, origin));
            }
        });
    }

    /**
     * This will generate a chunk if the state is unloaded
     */
    private void generateChunk(ChunkBlocks blocks, ChunkState state, ChunkOrigin origin) {
        for (var x = 0; x < ChunkHelper.CHUNK_BLOCK_SIZE; x++) {
            for (var z = 0; z < ChunkHelper.CHUNK_BLOCK_SIZE; z++) {
                if (x % 6 == 0 && z % 6 == 0 && Math.random() > 0.95f)
                    blocks.setBlock(x, 3, z, "skull_painting");
                blocks.setBlock(x, 0, z, "grass");
            }
        }
        state.state = State.BLOCKS_LOADED;
        logger.debug("Chunk[{},{},{}] blocks generated!", origin.x, origin.y, origin.z);
    }

    /**
     * @return returns a runnable that generates the mesh
     */
    private Runnable generateMesh(ChunkBlocks blocks, ChunkMesh mesh, ChunkState state, ChunkOrigin origin) {
        return () -> {
            blocks.foreachBlock((position, block) -> {
                var sideMeta = (byte) 0;
                for (var side : Side.values()) {
                    sideMeta = side.addSide(sideMeta);
                }
                block.addToChunk(position.x, position.y, position.z, sideMeta, atlas, mesh.meshData);
            });
            state.state = State.MESH_LOADED;
            logger.debug("Chunk[{},{},{}] mesh generated!", origin.x, origin.y, origin.z);
        };
    }
}
