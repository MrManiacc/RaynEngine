package com.jgfx.gui.systems;

import com.google.common.eventbus.Subscribe;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.glfw.WindowResizeEvent;
import com.jgfx.engine.injection.anotations.EventSubscriber;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.engine.assets.fbo.Fbo;
import com.jgfx.engine.utils.ShapeUtils;
import com.jgfx.engine.window.IWindow;
import com.jgfx.gui.components.camera.OrthoCameraCmp;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * The entity system
 */
//@AutoRegister
@EventSubscriber
public class GuiPreRenderer extends EntitySystem {
    @In GLUtils gl;
    @In ShapeUtils shapes;
    @In IWindow window;
    @Single("engine:entities#camera-2d") OrthoCameraCmp camera;
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
//        this.quad = shapes.get("engine:shapes#quad");
        buildScreenQuad(window.getWidth(), window.getHeight());
    }

    @Subscribe
    public void onWindowResize(WindowResizeEvent e) {
//        buildScreenQuad(window.getWidth(), window.getHeight());
//        GL11.glViewport(0, 0, (int) window.getWidth(), (int) window.getHeight());
    }

    /**
     * Render to the fbo
     */
    @Override
    protected void process(EngineTime time) {
        var width = window.getWidth();
        var height = window.getHeight();
        float aspect = width / (float) height;
//        camera.update();
        effectsFbo.bindToDraw();
        shader.start();
        gl.clear(true, true);
        //Here we render our elements
//        shader.loadMat4("viewProjMatrix", camera.viewProjection);
        shader.loadMat4("modelMatrix", new Matrix4f().identity().translate(0, 0, 0).scale(1));
        shader.loadFloat("time", time.getGameTime());
        shader.loadVec2("resolution", new Vector2f(window.getWidth(), window.getHeight()));
        quad.draw();
        shader.stop();
        effectsFbo.unbind();
        sceneFbo.bindToDraw();
        gl.color(0.1960784314f, 0.7921568627f, 0.6588235294f, 1.0f);
        gl.clear(true, true);
    }

    private void buildScreenQuad(float width, float height) {
        if (quad == null)
            quad = Vao.create(2, Vao.ARRAYS_TRIANGLE_STRIPS);
        //The quad
        {
            quad.bind();
            var hw = width;
//            hw *= window.getWidthScale();
            var hh = height;
            quad.createAttribute(new float[]{
                    -hw, hh,
                    -hw, -hh,
                    hw, hh,
                    hw, -hh}, 2);
            quad.createAttribute(new float[]{
                            0, 0,
                            1, 0,
                            0, 1,
                            1, 1},
                    2);
            quad.unbind();
        }
    }


    @Override
    public int compareTo(EntitySystem o) {
        if (o instanceof TransformResolver)
            return 1;
        return -1;
    }
}
