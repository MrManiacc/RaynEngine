package com.jgfx.chunk.systems;


import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.type.AssetManager;
import com.jgfx.chunk.data.ChunkMesh;
import com.jgfx.chunk.data.ChunkOrigin;
import com.jgfx.chunk.data.ChunkState;
import com.jgfx.chunk.utils.Groups;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.tiles.atlas.Atlas;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Resource;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.engine.utils.ShapeUtils;
import com.jgfx.player.data.PlayerCamera;
import com.jgfx.utils.State;
import org.joml.Matrix4f;

import java.util.Optional;

/**
 * This class will render the renderable chunks
 */
@AutoRegister
public class ChunkRenderer extends EntitySystem {
    @Resource("engine:shaders#chunk")
    private Shader shader;
    @Single("engine:entities#local-player")
    private EntityRef localPlayer;
    @In
    private ShapeUtils shapeUtils;
    @In
    private GLUtils glUtils;

    private Texture terrain;
    private Group chunks;

    /**
     * This is test code
     */
    @Override
    public void initialize() {
        this.chunks = Groups.CHUNK.group();
        this.terrain = Assets.get("engine:terrain#0", Texture.class).get();
    }


    /**
     * This is test code
     */
    @Override
    protected void process(EngineTime time) {
        shader.start();
        shader.loadMat4("projectionMatrix", localPlayer.getComponent(PlayerCamera.class).projectionMatrix);
        shader.loadMat4("viewMatrix", localPlayer.getComponent(PlayerCamera.class).viewMatrix);
        glUtils.alphaBlending(true);
        glUtils.cullBackFaces(true);
        glUtils.depthTest(true);
        terrain.bind();
        chunks.forEach(chunk -> {
            var state = chunk.getComponent(ChunkState.class);
            var origin = chunk.getComponent(ChunkOrigin.class);
            if (state.state == State.MODEL_LOADED) {
                var mesh = chunk.getComponent(ChunkMesh.class);
                shader.loadMat4("modelMatrix", origin.modelMatrix);
                mesh.vao.draw();
            }
        });
        terrain.unbind();
        glUtils.alphaBlending(false);
        glUtils.depthTest(false);
        shader.stop();
    }
}
