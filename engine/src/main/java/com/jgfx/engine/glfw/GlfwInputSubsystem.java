package com.jgfx.engine.glfw;


import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.game.GameEngine;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.input.Input;
import com.jgfx.engine.window.IWindow;
import lombok.Getter;

public class GlfwInputSubsystem implements EngineSubsystem {
    @Getter
    private final String name = "input";
    @In
    private IWindow window;
    private GlfwInput input;
    @In private GameEngine engine;
    @Getter private boolean loaded = false;


    @Override
    public void initialise() {
        this.input = new GlfwInput();
        //TODO: do some kind of check to confirm the window is glfw window
        input.registerCallbacks((GlfwWindow) window);
        CoreContext.put(Input.class, input);
        loaded = true;
    }

    /**
     * TODO: move this somewhere else, we shouldn't be checking for window shutdown here
     *
     * @param currentState The current state
     */
    @Override
    public void preUpdate() {
        if (input.keyPressed(Input.KEY_ESCAPE)) {
            engine.shutdown();
        }
    }

    /**
     * We want to reset the input after the update
     *
     * @param currentState The current state
     */
    @Override
    public void postUpdate() {
        input.reset();
    }
}
