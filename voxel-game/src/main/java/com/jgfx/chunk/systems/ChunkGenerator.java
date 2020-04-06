package com.jgfx.chunk.systems;

import com.google.common.eventbus.Subscribe;
import com.jgfx.blocks.Block;
import com.jgfx.chunk.data.ChunkBlocks;
import com.jgfx.chunk.data.ChunkOrigin;
import com.jgfx.chunk.data.ChunkState;
import com.jgfx.chunk.events.ChunkGeneratedEvent;
import com.jgfx.chunk.utils.ChunkHelper;
import com.jgfx.chunk.utils.Groups;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.EventSubscriber;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.utils.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * This class will generate the blocks for the chunk
 */
@EventSubscriber
@AutoRegister
public class ChunkGenerator extends EntitySystem {
    private Group chunks;
    private final Logger logger;

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
            var state = chunk.getComponent(ChunkState.class);
            var origin = chunk.getComponent(ChunkOrigin.class);
            if (state.state == State.UNLOADED) {
                generateChunk(chunk.getComponent(ChunkBlocks.class), state);
                logger.debug("Chunk[{},{},{}] blocks generated!", origin.x, origin.y, origin.z);
            }
        });
    }

    /**
     * This will generate a chunk if the state is unloaded
     */
    private void generateChunk(ChunkBlocks blocks, ChunkState state) {
        var stair = "engine:blocks#stone_stair";
        var grass = "engine:blocks#grass";
        var random = new Random();
        for (var x = 0; x < ChunkHelper.CHUNK_BLOCK_SIZE; x++) {
            for (var z = 0; z < ChunkHelper.CHUNK_BLOCK_SIZE; z++) {
                if (x % 6 == 0 && z % 6 == 0 && Math.random() > 0.95f)
                    blocks.setBlock(x, 1, z, stair);
                blocks.setBlock(x, 0, z, grass);
            }
        }
        state.state = State.BLOCKS_LOADED;
    }
}
