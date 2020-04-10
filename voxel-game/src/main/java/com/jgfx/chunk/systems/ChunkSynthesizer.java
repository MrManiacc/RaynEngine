package com.jgfx.chunk.systems;

import com.jgfx.chunk.data.*;
import com.jgfx.chunk.events.ChunkGeneratedEvent;

import static com.jgfx.chunk.utils.Groups.*;

import com.jgfx.debug.shapes.LineShape;
import com.jgfx.debug.shapes.MultiShape;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.event.Bus;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.EventSubscriber;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.input.Input;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.player.data.PlayerTransform;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
        generateChunk(0, 0, 0);
        generateChunk(32, 0, 0);
        generateChunk(-32, 0, 0);
        generateChunk(0, 0, 32);
        generateChunk(0, 0, -32);


        //        generateChunk(0, 32, 0);
//        generateChunk(32, 32, 0);
//        generateChunk(-32, 0, 0, new CubeShape(new Vector4f(1)));
//        generateChunk(0, 0, 32, new CubeShape(new Vector4f(1)));
//        generateChunk(0, 0, -32, new OutlineShape(new Vector2f(0), new Vector2f(1)));
//        generateChunk(0, -32, 0, new OutlineShape(new Vector2f(0), new Vector2f(1)));
    }

    /**
     * Here we're going to generate new chunks, when they're needed
     */
    @Override
    protected void process(EngineTime time) {
        var position = localPlayer.get(PlayerTransform.class);
        if (input.keyPressed(Input.KEY_G)) {
//            var entity = generateChunk((int) position.x, (int) position.y, (int) position.z, new LineShape(32, 0.025f));
        }
    }

    /**
     * Generates a chunk at the given position, sends out an event and returns the chunk
     */
    private EntityRef generateChunk(int x, int y, int z) {
        var entity = world.createEntity();
        entity.add(new ChunkOrigin(x, y, z));
        entity.add(new ChunkBlocks());
        entity.add(new ChunkMesh());
        entity.add(new ChunkNeighbors(entity));
        entity.add(new ChunkState());
        entity.add(new MultiShape(new Vector3f(x, y, z), new Vector4f(1, 1, 1, 1),
                new LineShape(new Vector3f(-0.5f, 15.5f, -0.5f), new Vector3f(0, 0, 0), 32, 0.25f),
                new LineShape(new Vector3f(31.5f, 15.5f, -0.5f), new Vector3f(0, 0, 0), 32, 0.25f),
                new LineShape(new Vector3f(31.5f, 15.5f, 31.5f), new Vector3f(0, 0, 0), 32, 0.25f),
                new LineShape(new Vector3f(-0.5f, 15.5f, 31.5f), new Vector3f(0, 0, 0), 32, 0.25f)
        ));
        Bus.LOGIC.post(new ChunkGeneratedEvent(entity));
        return entity;
    }


}
