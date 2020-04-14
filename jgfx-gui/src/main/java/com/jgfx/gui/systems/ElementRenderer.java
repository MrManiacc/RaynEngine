package com.jgfx.gui.systems;

import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.*;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.engine.utils.ShapeUtils;
import com.jgfx.engine.window.IWindow;
import com.jgfx.gui.components.camera.OrthoCameraCmp;
import com.jgfx.gui.components.color.BackgroundColorCmp;
import com.jgfx.gui.components.display.render.RenderableCmp;
import com.jgfx.gui.components.display.rotate.RotationCmp;
import com.jgfx.gui.components.image.ImageCmp;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.elements.containers.IContainer;
import com.jgfx.gui.elements.containers.Root;

/**
 * Renders all elements
 */
@AutoRegister
public class ElementRenderer extends EntitySystem {
    @In GLUtils gl;
    @In ShapeUtils shapes;
    @In IWindow window;
    @Resource("engine:shaders#gui")
    Shader shader;
    @All({RenderableCmp.class})
    @One({BackgroundColorCmp.class, ImageCmp.class})
    Group elements;

    @Single("engine:entities#camera-2d")
    OrthoCameraCmp camera;
    @In
    Root root;

    @Override
    protected void process(EngineTime time) {
        shader.start();
        root.list().forEach(this::draw);
        shader.stop();
    }

    private void draw(IElement element) {
        gl.alphaBlending(true);
        drawElement(element.entity());
        if (element.is(IContainer.class)) {
            var container = (IContainer) element;
            for (var object : container.list()) {
                var child = (IElement) object;
                draw(child);
            }
        }
        gl.alphaBlending(false);
    }

    private void drawElement(EntityRef entity) {
        if (entity.has(RenderableCmp.class)) {
            var renderCmp = entity.get(RenderableCmp.class);

            var matrix = camera.apply(renderCmp.modelMatrix());
            shader.loadMat4("modelMatrix", matrix);
            entity.ifPresentOrElse(BackgroundColorCmp.class, color -> {
                shader.loadVec4("color", color.getColor());
                shader.loadBool("isColored", true);
            }, () -> shader.loadBool("isColored", false));
            entity.ifPresentOrElse(ImageCmp.class, image -> {
                image.getTexture().bind();
                shader.loadBool("isTextured", true);
            }, () -> shader.loadBool("isTextured", false));
            renderCmp.getVao().draw();
        }
    }


    /**
     * we want this to be the first component so we can clear the window
     */
    @Override
    public int compareTo(EntitySystem o) {
        return -1;
    }
}
