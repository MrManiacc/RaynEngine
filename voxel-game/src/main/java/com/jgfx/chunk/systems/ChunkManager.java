package com.jgfx.chunk.systems;

import com.google.common.eventbus.Subscribe;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.chunk.data.*;
import com.jgfx.chunk.events.ChunkDestroyedEvent;
import com.jgfx.chunk.events.ChunkGeneratedEvent;
import com.jgfx.chunk.utils.ChunkHelper;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.ref.NullEntityRef;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.EventSubscriber;
import com.jgfx.utils.State;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * This class will handle the chunks by storing their position with the entity ref
 */
@AutoRegister
@EventSubscriber
public class ChunkManager extends EntitySystem {
    private final TLongObjectMap<EntityRef> entityMap;
    private final Logger logger;

    /**
     * We simply want to store the chunk manager ASAP so we can have it injected into other systems
     */
    public ChunkManager() {
        CoreContext.put(ChunkManager.class, this);
        this.entityMap = new TLongObjectHashMap<>();
        this.logger = LogManager.getLogger(ChunkManager.class);
    }

    /**
     * This will map the entity with it's position as a long inside the entity map
     */
    @Subscribe
    public void onChunkCreated(ChunkGeneratedEvent event) {
        var origin = event.chunk.get(ChunkOrigin.class);
        if (entityMap.containsKey(origin.index)) {
            event.chunk.dispose();
            logger.warn("Chunk[{}] at {}, {}, {} was generated twice. Destroying", origin.index, origin.x, origin.y, origin.z);
        } else {
            entityMap.put(origin.index, event.chunk);
            logger.debug("Chunk[{}] at {}, {}, {} stored in chunk manager", origin.index, origin.x, origin.y, origin.z);
        }
    }

    /**
     * This will destroy the chunk
     */
    @Subscribe
    public void onChunkDestroyed(ChunkDestroyedEvent event) {
        var origin = event.chunk.get(ChunkOrigin.class);
        if (entityMap.containsKey(origin.index)) {
            entityMap.remove(origin.index);
            logger.debug("Chunk[{}] at {}, {}, {} removed from chunk manager", origin.index, origin.x, origin.y, origin.z);
        } else
            logger.warn("Chunk[{}] at {}, {}, {} destroyed but wasn't in the chunk manager", origin.index, origin.x, origin.y, origin.z);
        event.chunk.dispose();
    }

    /**
     * @return gets the chunk entity at the given position
     */
    public EntityRef getChunk(int x, int y, int z) {
        var index = ChunkHelper.getIndex(x, y, z);
        if (entityMap.containsKey(index))
            return entityMap.get(index);
        return NullEntityRef.NULL;
    }

    /**
     * @return returns the chunk blocks at the given coordinate, this is a helper
     */
    public Optional<ChunkBlocks> getBlocks(int x, int y, int z) {
        var chunk = getChunk(x, y, z);
        if (chunk == EntityRef.NULL)
            return Optional.empty();
        return Optional.of(chunk.get(ChunkBlocks.class));
    }

    /**
     * @return gets the neighbors for a given chunk or empty
     */
    public Optional<ChunkNeighbors> getNeighbors(int x, int y, int z) {
        var chunk = getChunk(x, y, z);
        if (chunk == EntityRef.NULL)
            return Optional.empty();
        return Optional.of(chunk.get(ChunkNeighbors.class));
    }


    /**
     * @return gets the mesh for a given chunk or empty
     */
    public Optional<ChunkMesh> getMesh(int x, int y, int z) {
        var chunk = getChunk(x, y, z);
        if (chunk == EntityRef.NULL)
            return Optional.empty();
        return Optional.of(chunk.get(ChunkMesh.class));
    }

    /**
     * @return gets the state of a chunk, if it's not present we return unloaded automatically
     */
    public State getState(int x, int y, int z) {
        var chunk = getChunk(x, y, z);
        if (chunk == EntityRef.NULL)
            return State.UNLOADED;
        return chunk.get(ChunkState.class).state;
    }

}
