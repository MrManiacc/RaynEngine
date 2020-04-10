package com.jgfx.chunk.data;

import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.blocks.Block;
import com.jgfx.blocks.Blocks;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.chunk.utils.ChunkHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3i;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * Stores the data about blocks
 */
public class ChunkBlocks implements Component {
    private final byte[] blocks;
    private static final Logger logger = LogManager.getLogger(ChunkBlocks.class);;

    public ChunkBlocks() {
        this.blocks = new byte[ChunkHelper.CHUNK_SIZE_CUBED];
    }

    /**
     * @return returns the block id at the given position
     */
    public Optional<Byte> getBlockId(int x, int y, int z) {
        var index = (int) ChunkHelper.getIndex(x, y, z);
        if(index >= 0 && index < blocks.length)
            return Optional.of(blocks[index]);
        return Optional.empty();
    }

    /**
     * @return returns the actual block
     */
    public Optional<Block> getBlock(int x, int y, int z) {
        var output = new AtomicReference<Optional<Block>>();
        getBlockId(x, y, z).ifPresentOrElse(id -> output.set(Blocks.getBlock(id)), ()-> output.set(Optional.empty()));
        return output.get();
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
        Blocks.getBlock(urn).ifPresent(block -> {
            setBlock(x, y, z, (byte) block.getId());
        });
    }

    /**
     * Sets a block at the given position to the given type
     */
    public void setBlock(int x, int y, int z, String input) {
        Blocks.getBlock(input).ifPresent(block -> {
            setBlock(x, y, z, (byte) block.getId());
        });
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
}
