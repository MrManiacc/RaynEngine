package com.jgfx.gui.systems;

import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.fbo.Fbo;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.engine.utils.ShapeUtils;
import com.jgfx.engine.window.IWindow;
import com.jgfx.gui.camera.CameraOrthoCmp;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * This will render the proper stuff to screen
 */
public class PostGuiRenderer extends EntitySystem {
    private Fbo scene, effects;
    private Shader shader;
    private Vao quadVao;
    @In ShapeUtils shapes;
    @In GLUtils gl;
    @In IWindow window;
    @Single("engine:entities#camera-2d")
    CameraOrthoCmp camera;

    /**
     * We initialize our fbo
     */
    @Override
    public void initialize() {
        this.scene = Assets.get("engine:fbos#scene", Fbo.class).get();
        this.effects = Assets.get("engine:fbos#effects", Fbo.class).get();
        this.shader = Assets.get("engine:shaders#fbo", Shader.class).get();
        this.quadVao = shapes.get("engine:shapes#quad");
    }

    /**
     * Here we render the fbo
     */
    @Override
    protected void process(EngineTime time) {
        scene.unbind();
        gl.color(0.1960784314f, 0.3921568627f, 0.6588235294f, 1.0f);
        gl.clear(false, true);
        gl.alphaBlending(true);
        var width = window.getFbWidth() / 2.0f;
        var height = window.getFbHeight() / 2.0f;
        camera.update();
        scene.bindTexture();
        shader.start();
        shader.loadFloat("time", time.getGameTime());
        shader.loadVec2("resolution", new Vector2f(width * 2, height * 2));
        shader.loadMat4("viewProjMatrix", camera.viewProjection);
        shader.loadMat4("modelMatrix", new Matrix4f().identity().translate(0, 0, 0).scale(width, height, 1).rotateZ((float) Math.toRadians(90)));
        quadVao.draw();
        shader.stop();
        scene.unbindTexture();
    }

    /**
     * @return returns 1 so we can be the last system
     */
    @Override
    public int compareTo(EntitySystem o) {
        return 1;
    }
}
