package com.jgfx.engine.glfw;


import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.assets.config.Config;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.game.GameEngine;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Resource;
import com.jgfx.engine.utils.GLUtils;
import com.jgfx.engine.window.IWindow;
import lombok.Getter;

public class GlfwWindowSubsystem implements EngineSubsystem {
    @Getter
    private final String name = "graphics";
    private GlfwWindow window;
    @In private GameEngine engine;
    @In private GLUtils glUtils;
    @Getter private boolean loaded = false;
    @Resource("engine:config#window") private Config config;


    @Override
    public void preInitialise() {
        glUtils = CoreContext.put(new GLUtils());
        //TODO: create the window here, and upload it to the context
        this.window = CoreContext.put(IWindow.class, new GlfwWindow("ChunkWorld", 1350, 900, false, true, false));
        window.init();
    }

    @Override
    public void initialise() {
        loaded = true;
        window.setSize(config.getInt("window", "width"), config.getInt("window", "height"));
        window.setTitle(config.getString("window", "title"));
        window.setResizable(config.getBoolean("window", "resizable"));
    }

    /**
     * Check to see if we need to request a shutdown
     *
     * @param currentState The current state
     */
    @Override
    public void preUpdate() {
        if (window.isCloseRequested())
            engine.shutdown();
        if (glUtils != null) {
            glUtils.color(0.1960784314f, 0.3921568627f, 0.6588235294f, 1.0f);
            glUtils.clear(true, true);
        }
    }

    /**
     * We want to update the window here
     *
     * @param currentState The current state
     */
    @Override
    public void postUpdate() {
        window.process();
    }
}
