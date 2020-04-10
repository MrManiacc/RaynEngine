package com.jgfx.chunk.systems;


import com.jgfx.chunk.data.*;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.All;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Resource;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.gui.systems.GuiPreRenderer;
import com.jgfx.player.data.PlayerCamera;
import com.jgfx.utils.State;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This class will render the renderable chunks
 */
@AutoRegister(after = GuiPreRenderer.class)
public class ChunkRenderer extends EntitySystem {
    @Resource("engine:shaders#chunk") Shader shader;
    @Single("engine:entities#local-player") EntityRef localPlayer;
    @In GLUtils glUtils;
    @Resource("engine:terrain#0") Texture terrain;
    @All({ChunkBlocks.class, ChunkMesh.class, ChunkNeighbors.class, ChunkOrigin.class, ChunkState.class}) Group chunks;

    /**
     * This is test code
     */
    @Override
    protected void process(EngineTime time) {
        chunks.forEach(beginRender, render, checkState, endRender);
    }

    /**
     * Called before render of entities
     */
    private Runnable beginRender = () -> {
        shader.start();
        shader.loadMat4("projectionMatrix", localPlayer.get(PlayerCamera.class).projectionMatrix);
        shader.loadMat4("viewMatrix", localPlayer.get(PlayerCamera.class).viewMatrix);
        glUtils.alphaBlending(true);
        glUtils.cullBackFaces(true);
        glUtils.depthTest(true);
        terrain.bind();
    };
    /**
     * Renders the individual chunks
     */
    private Consumer<EntityRef> render = chunk -> {
        var origin = chunk.get(ChunkOrigin.class);
        var mesh = chunk.get(ChunkMesh.class);
        shader.loadMat4("modelMatrix", origin.modelMatrix);
        mesh.vao.draw();
    };


    /**
     * this will check the state of the chunk
     */
    private Predicate<EntityRef> checkState = chunk -> chunk.get(ChunkState.class).state == State.MODEL_LOADED;

    /**
     * Called after the render
     */
    private Runnable endRender = () -> {
        terrain.unbind();
        glUtils.alphaBlending(false);
        glUtils.depthTest(false);
        shader.stop();
    };
}
