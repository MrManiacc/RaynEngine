package com.jgfx.chunk.systems;

import com.jgfx.engine.assets.fbo.Fbo;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Resource;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.engine.utils.ShapeUtils;
import com.jgfx.player.data.PlayerCamera;
import org.joml.Matrix4f;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class will render a cube with the fbo output
 */
@AutoRegister(after = ChunkRenderer.class)
public class ChunkSpecialRenderer extends EntitySystem {
    @Single("engine:entities#local-player") PlayerCamera camera;
    @Resource("engine:shaders#chunk") Shader shader;
    @Resource("engine:fbos#effects") Fbo effectsFbo;
    @In GLUtils gl;
    @In ShapeUtils shapes;
    private Vao testShape;

    @Override
    public void initialize() {
        this.testShape = shapes.get("engine:shapes#quad");
    }

    @Override
    protected void process(EngineTime time) {
        effectsFbo.bindTexture();
        gl.cullBackFaces(false);
        gl.depthTest(true);
        shader.start();
        shader.loadMat4("projectionMatrix", camera.projectionMatrix);
        shader.loadMat4("viewMatrix", camera.viewMatrix);
        shader.loadMat4("modelMatrix", new Matrix4f().identity().translate(0, 20, -20).scale(10));
        testShape.draw();
        shader.stop();
        gl.depthTest(false);
        gl.cullBackFaces(true);
        effectsFbo.unbindTexture();

    }
}
