package com.jgfx.gui.systems;

import com.google.common.eventbus.Subscribe;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.fbo.Fbo;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.glfw.WindowResizeEvent;
import com.jgfx.engine.injection.anotations.EventSubscriber;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.engine.utils.ShapeUtils;
import com.jgfx.engine.window.IWindow;
import com.jgfx.gui.components.camera.OrthoCameraCmp;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

/**
 * This will render the proper stuff to screen
 */
//@AutoRegister
@EventSubscriber
public class GuiPostRenderer extends EntitySystem {
    private Fbo scene;
    private Shader shader;
    private Vao quadVao;
    @In ShapeUtils shapes;
    @In GLUtils gl;
    @In IWindow window;
    @Single("engine:entities#camera-2d")
    OrthoCameraCmp camera;

    /**
     * We initialize our fbo
     */
    @Override
    public void initialize() {
        this.scene = Assets.get("engine:fbos#effects", Fbo.class).get();
        this.shader = Assets.get("engine:shaders#fbo", Shader.class).get();
        buildScreenQuad(window.getWidth(), window.getHeight());
    }

    private void buildScreenQuad(float width, float height) {
        if (quadVao == null)
            quadVao = Vao.create(2, Vao.ARRAYS_TRIANGLE_STRIPS);
        //The quad
        {
            quadVao.bind();
            var hw = width;
//            hw *= window.getWidthScale();
            var hh = height;
            quadVao.createAttribute(new float[]{
                    -hw, hh,
                    -hw, -hh,
                    hw, hh,
                    hw, -hh}, 2);
            quadVao.createAttribute(new float[]{
                            0, 0,
                            1, 0,
                            0, 1,
                            1, 1},
                    2);
            quadVao.unbind();
        }
    }


    @Subscribe
    public void onWindowResize(WindowResizeEvent e) {
        buildScreenQuad(window.getWidth(), window.getHeight());
        GL11.glViewport(0, 0, (int) window.getWidth(), (int) window.getHeight());
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
        var width = window.getWidth();
        var height = window.getHeight();
        float aspect = width / (float) height;
//        camera.update();
        scene.bindTexture();
        shader.start();
        shader.loadFloat("time", time.getGameTime());
        shader.loadVec2("resolution", new Vector2f(width, height));
//        shader.loadMat4("viewProjMatrix", camera.viewProjection);
        shader.loadMat4("modelMatrix", new Matrix4f().identity().translate(0, 0, 0).scale(aspect, aspect, 1));
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
