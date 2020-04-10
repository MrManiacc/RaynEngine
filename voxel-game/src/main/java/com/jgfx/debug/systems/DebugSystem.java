package com.jgfx.debug.systems;

import com.jgfx.chunk.systems.ChunkRenderer;
import com.jgfx.debug.shapes.IShape;
import com.jgfx.debug.shapes.LineShape;
import com.jgfx.debug.shapes.MultiShape;
import com.jgfx.engine.assets.fbo.Fbo;
import com.jgfx.engine.assets.shader.Shader;
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
import com.jgfx.engine.window.IWindow;
import com.jgfx.player.data.PlayerCamera;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Consumer;

@AutoRegister(after = ChunkRenderer.class)
public class DebugSystem extends EntitySystem {

    @All(IShape.class) private Group shapes;
    @Single("engine:entities#local-player") PlayerCamera playerCamera;
    @Resource("engine:shaders#chunk") Shader shader;
    @Resource("engine:fbos#effects") Fbo effectsFbo;
    @In IWindow window;
    @In GLUtils glUtils;

    /**
     * This will render the shape outlines
     */
    @Override
    protected void process(EngineTime time) {
        //Here
        build();
        render();
    }

    /**
     * This will build all of the un built shapes
     */
    private void build() {
        shapes.forEach(
                entity -> entity.get(IShape.class).build(),
                entity -> !entity.get(IShape.class).isLoaded()
        );
    }

    /**
     * Renders all of the shapes
     */
    private void render() {
        shapes.forEach(
                beginRender,
                render,
                entity -> entity.get(IShape.class).isLoaded(),
                endRender
        );
    }

    /**
     * Start the render process
     */
    private Runnable beginRender = () -> {
        glUtils.depthTest(true);
        glUtils.cullBackFaces(false);
        shader.start();
        shader.loadMat4("projectionMatrix", playerCamera.projectionMatrix);
        shader.loadMat4("viewMatrix", playerCamera.viewMatrix);
        effectsFbo.bindTexture();
    };


    /**
     * Renders an outline with the given entities
     */
    private Consumer<EntityRef> render = entity -> {
        var shape = entity.get(IShape.class);
        shape.draw(shader);
    };

    /**
     * Ends the rendering
     */
    private Runnable endRender = () -> {
        glUtils.depthTest(false);
        shader.stop();
        effectsFbo.unbindTexture();
    };
}

