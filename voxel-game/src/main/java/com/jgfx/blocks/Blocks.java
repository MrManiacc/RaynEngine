package com.jgfx.blocks;

import com.google.common.collect.Maps;
import com.jgfx.assets.naming.Name;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.injection.anotations.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

/**
 * This class allows for static access of blocks via their id and urn
 */
public class Blocks {
    private static final Map<ResourceUrn, Block> blocks = Maps.newConcurrentMap();
    private static final Map<Long, ResourceUrn> urnIds = Maps.newConcurrentMap();
    private static final Map<Name,Long> namedBlocks = Maps.newConcurrentMap();
    private static final Logger logger = LogManager.getLogger(Blocks.class);

    /**
     * @return returns a block by the given resource urn
     */
    public static Optional<Block> getBlock(ResourceUrn urn) {
        return Optional.ofNullable(blocks.get(urn));
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
     * @return returns a block by the correct blockId or empty
     */
    public static Optional<Block> getBlock(long blockId) {
        if (urnIds.containsKey(blockId)) {
            var urn = urnIds.get(blockId);
            return getBlock(urn);
        }
        return Optional.empty();
    }

    /**
     * @return returns a block by the correct blockId or empty
     */
    public static Optional<Block> getBlock(Name blockId) {
        if (namedBlocks.containsKey(blockId)) {
            var id = namedBlocks.get(blockId);
            return getBlock(id);
        }
        return Optional.empty();
    }

    /**
     * @return  this method will check to see if the input is a name or a resource urn, and grab the block correctly
     */
    public static Optional<Block> getBlock(String blockId) {
        var isUrn = ResourceUrn.isValid(blockId);
        if(isUrn)
            return getBlock(new ResourceUrn(blockId));
        return getBlock(new Name(blockId));
    }

    /**
     * Loads a block
     */
    public static void addBlock(Block block) {
        var name = block.getUrn().getFragmentName();
        blocks.remove(block.getUrn());
        urnIds.remove(block.getId());
        if(namedBlocks.containsKey(name)){
            logger.warn("Block with name {} conflicts with {}, use the full urn '{}'", name, getBlock(namedBlocks.get(name)).get().getUrn().toString(), block.getUrn().toString());
        }else{
            logger.info("Mapped block with urn {}, to name {}", block.getUrn(), name);
            namedBlocks.put(name, block.getId());
        }
        blocks.put(block.getUrn(), block);
        urnIds.put(block.getId(), block.getUrn());
    }
}
