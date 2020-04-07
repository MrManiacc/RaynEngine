package com.jgfx.gui.systems;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.type.AssetManager;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.injection.anotations.All;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Resource;
import com.jgfx.engine.utils.ShapeUtils;
import com.jgfx.gui.components.RectCmp;

/**
 * The entity system
 */
public class GuiRenderer extends EntitySystem {
    @All(RectCmp.class)
    private Group rects;
    @Resource("engine:shaders#gui")
    private Shader shader;
    @In
    private ShapeUtils shapes;
    private Vao quadVao;


    /**
     * Here we can initialize things
     */
    @Override
    public void initialize() {
        this.quadVao = shapes.get("engine:shapes#quad");
    }
}
