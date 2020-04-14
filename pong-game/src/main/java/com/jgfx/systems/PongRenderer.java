package com.jgfx.systems;

import com.jgfx.assets.type.AssetManager;
import com.jgfx.components.CameraComponent;
import com.jgfx.components.MaterialComponent;
import com.jgfx.components.Physics2dComponent;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.All;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Resource;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.engine.utils.ShapeUtils;

@AutoRegister
public class PongRenderer extends EntitySystem {
    @All({Physics2dComponent.class, MaterialComponent.class}) private Group entities;
    @Resource("engine:shaders#gui") private Shader shader;
    @In private ShapeUtils shapeUtils;
    @In private AssetManager assetManager;
    @In private GLUtils glUtils;
    @Single("engine:entities#local-player") private EntityRef player;

    /**
     * Renders the paddles
     */
    @Override
    protected void process(EngineTime time) {
        shader.start();
        shader.loadMat4("projectionMatrix", player.get(CameraComponent.class).projectionMatrix);
        glUtils.alphaBlending(true);
        entities.forEach(entity -> {
            var physics = entity.get(Physics2dComponent.class);
            var materialRef = entity.get(MaterialComponent.class);
            var shape = shapeUtils.get(materialRef.shapeUrn);
            var texture = assetManager.getAsset(materialRef.textureUrn, Texture.class).get();
            texture.bind();
            shader.loadMat4("modelMatrix", physics.getModelMatrix());
            shape.draw();
            texture.unbind();
        });
        glUtils.alphaBlending(false);
        shader.stop();
    }

}
