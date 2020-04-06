package com.jgfx.blocks;

import com.google.common.collect.Maps;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.injection.anotations.Resource;

import java.util.Map;
import java.util.Optional;

/**
 * This class allows for static access of blocks via their id and urn
 */
public class Blocks {
    private static final Map<ResourceUrn, Block> blocks = Maps.newConcurrentMap();
    private static final Map<Long, ResourceUrn> urnIds = Maps.newConcurrentMap();

    /**
     * @return returns a block by the given resource urn
     */
    public static Optional<Block> getBlock(ResourceUrn urn) {
        return Optional.ofNullable(blocks.get(urn));
    }

    /**
     * @return returns the block id as a byte from the resource urn
     */
    public static Optional<Byte> getBlockId(ResourceUrn resourceUrn) {
        var block = getBlock(resourceUrn);
        if (block.isEmpty()) return Optional.empty();
        var id = (byte) block.get().getId();
        return Optional.of(id);
    }

    /**
     * @return returns a block by the correct blockId or empty
     */
    public static Optional<Block> getBlock(byte blockId) {
        if (urnIds.containsKey((long) blockId)) {
            var urn = urnIds.get((long) blockId);
            return getBlock(urn);
        }
        return Optional.empty();
    }

    /**
     * Loads a block
     */
    public static void addBlock(Block block) {
        blocks.remove(block.getUrn());
        urnIds.remove(block.getId());
        blocks.put(block.getUrn(), block);
        urnIds.put(block.getId(), block.getUrn());
    }
}
