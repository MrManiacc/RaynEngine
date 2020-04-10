package com.jgfx.gui.systems;

import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Resource;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.engine.assets.fbo.Fbo;
import com.jgfx.engine.utils.ShapeUtils;
import com.jgfx.engine.window.IWindow;
import com.jgfx.gui.camera.CameraOrthoCmp;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * The entity system
 */
@AutoRegister
public class GuiRenderer extends EntitySystem {
    @In GLUtils gl;
    @In ShapeUtils shapes;
    @In IWindow window;
    @Single("engine:entities#camera-2d") CameraOrthoCmp camera;
    private Fbo sceneFbo;
    private Fbo effectsFbo;
    private Shader shader;
    private Vao quad;

    /**
     * Here we can initialize things
     */
    @Override
    public void initialize() {
        this.sceneFbo = Assets.get("engine:fbos#scene", Fbo.class).get();
        this.effectsFbo = Assets.get("engine:fbos#effects", Fbo.class).get();
        this.shader = Assets.get("engine:shaders#clouds", Shader.class).get();
        this.quad = shapes.get("engine:shapes#quad");
    }

    /**
     * Render to the fbo
     */
    @Override
    protected void process(EngineTime time) {
        camera.update();
        effectsFbo.bindToDraw();
        shader.start();
        gl.clear(true, true);
        //Here we render our elements
        shader.loadMat4("viewProjMatrix", camera.viewProjection);
        shader.loadMat4("modelMatrix", new Matrix4f().identity().scale(window.getFbWidth(), window.getFbWidth(), 1));
        shader.loadFloat("time", time.getGameTime());
        shader.loadVec2("resolution", new Vector2f(window.getFbWidth(), window.getFbHeight()));
        quad.draw();
        shader.stop();
        effectsFbo.unbind();
        sceneFbo.bindToDraw();
        gl.color(0.1960784314f, 0.7921568627f, 0.6588235294f, 1.0f);
        gl.clear(true, true);
    }

    @Override
    public int compareTo(EntitySystem o) {
        if (o instanceof GuiTransformResolver)
            return 1;
        return -1;
    }
}
