package com.jgfx.chunk.data;

import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.blocks.Block;
import com.jgfx.blocks.Blocks;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.chunk.utils.ChunkHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3i;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Stores the data about blocks
 */
public class ChunkBlocks implements Component {
    private final byte[] blocks;
    private Logger logger;

    public ChunkBlocks() {
        this.blocks = new byte[ChunkHelper.CHUNK_SIZE_CUBED];
        this.logger = LogManager.getLogger(ChunkBlocks.class);
    }

    /**
     * @return returns the block id at the given position
     */
    public byte getBlockId(int x, int y, int z) {
        return blocks[(int) ChunkHelper.getIndex(x, y, z)];
    }

    /**
     * @return returns the actual block
     */
    public Optional<Block> getBlock(int x, int y, int z) {
        var blockId = getBlockId(x, y, z);
        return Blocks.getBlock(blockId);
    }

    /**
     * @return returns the block id at the given position
     */
    public byte getBlockId(Vector3i position) {
        return blocks[(int) ChunkHelper.getIndex(position)];
    }

    /**
     * Sets a block at the given position to the given type
     */
    public void setBlock(int x, int y, int z, byte block) {
        var index = ChunkHelper.getIndex(x, y, z);
        blocks[(int) index] = block;
    }

    /**
     * Sets a block to the given urn
     */
    public void setBlock(int x, int y, int z, ResourceUrn urn) {
        Blocks.getBlockId(urn).ifPresent(id -> {
            setBlock(x, y, z, id);
        });
    }

    /**
     * Sets a block at the given position to the given type
     */
    public void setBlock(int x, int y, int z, String urn) {
        setBlock(x, y, z, new ResourceUrn(urn));
    }

    /**
     * Iterates each block
     */
    public void foreachBlock(BiConsumer<Vector3i, Block> consumer) {
        for (var i = 0; i < blocks.length; i++) {
            var blockId = blocks[i];
            if (blockId != 0) {
                var position = ChunkHelper.getPositionVec(i);
                Blocks.getBlock(blockId).ifPresent(block -> consumer.accept(position, block));
            }
        }
    }

    /**
     * Sets a block at the given position to the given type
     */
    public void setBlock(Vector3i position, byte block) {
        var index = ChunkHelper.getIndex(position);
        blocks[(int) index] = block;
    }
}
