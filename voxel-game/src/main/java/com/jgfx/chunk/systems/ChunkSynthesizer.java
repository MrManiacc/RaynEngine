package com.jgfx.chunk.systems;

import com.jgfx.chunk.data.*;
import com.jgfx.chunk.events.ChunkGeneratedEvent;
import com.jgfx.chunk.utils.ChunkHelper;
import com.jgfx.chunk.utils.Groups;

import static com.jgfx.chunk.utils.Groups.*;

import com.jgfx.engine.ecs.World;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.ecs.group.GroupBuilder;
import com.jgfx.engine.event.Bus;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.All;
import com.jgfx.engine.injection.anotations.EventSubscriber;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.input.Input;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.player.data.PlayerTransform;
import com.jgfx.utils.GroupsBuilder;
import org.joml.Vector3i;

import static com.jgfx.chunk.utils.ChunkHelper.*;

/**
 * This class will generate chunks around the player
 */
@AutoRegister
@EventSubscriber
public class ChunkSynthesizer extends EntitySystem {
    @Single("engine:entities#local-player") private EntityRef localPlayer;
    @In private Input input;
    private Group chunks;
    //Stores the last time a chunk was generated
    private long lastGenTime;

    /**
     * We create the chunk group here so we can reuse the groups instead of typing it out every time
     */
    @Override
    public void initialize() {
        this.chunks = CHUNK.group();
        generateChunks(new Vector3i(0, 0, 0), 10);
    }

    /**
     * Here we're going to generate new chunks, when they're needed
     */
    @Override
    protected void process(EngineTime time) {
        var position = localPlayer.getComponent(PlayerTransform.class);
        if (input.keyPressed(Input.KEY_G)) {
//            var entity = generateChunk((int) position.x, (int) position.y, (int) position.z);
        }
    }

    /**
     * Generates chunks starting for the center, spreading out in each direction by the total
     */
    private void generateChunks(Vector3i position, int total) {
        var startX = position.x - ((total * CHUNK_BLOCK_SIZE) / 2);
        var stopX = position.x + ((total * CHUNK_BLOCK_SIZE) / 2);

        var startY = position.y - ((total * CHUNK_BLOCK_SIZE) / 2);
        var stopY = position.y + ((total * CHUNK_BLOCK_SIZE) / 2);

        var startZ = position.z - ((total * CHUNK_BLOCK_SIZE) / 2);
        var stopZ = position.z + ((total * CHUNK_BLOCK_SIZE) / 2);

        for (int x = startX; x < stopX; x += CHUNK_BLOCK_SIZE) {
            for (int y = startY; y < stopY; y += CHUNK_BLOCK_SIZE) {
                for (int z = startZ; z < stopZ; z += CHUNK_BLOCK_SIZE) {
                    generateChunk(x, y, z);
                }
            }
        }
    }

    /**
     * Generates a chunk at the given position, sends out an event and returns the chunk
     */
    private EntityRef generateChunk(int x, int y, int z) {
        var entity = world.createEntity();
        entity.addComponent(new ChunkOrigin(x, y, z));
        entity.addComponent(new ChunkBlocks());
        entity.addComponent(new ChunkMesh());
        entity.addComponent(new ChunkNeighbors(entity));
        entity.addComponent(new ChunkState());
        Bus.LOGIC.post(new ChunkGeneratedEvent(entity));
        return entity;
    }


}
