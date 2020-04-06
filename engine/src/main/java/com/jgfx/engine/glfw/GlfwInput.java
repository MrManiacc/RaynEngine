package com.jgfx.engine.glfw;

import com.jgfx.engine.input.Input;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

/**
 * Represents the generic input
 */
public class GlfwInput extends Input {

    /**
     * Initialize the callbacks for input
     */
    public void registerCallbacks(GlfwWindow window) {
        window.registerKeyCallback(new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keyStates[key] = action;
            }
        });
        window.registerMouseButtonCallback(new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseStates[button] = action;
            }
        });
        window.registerMousePositionCallback(new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                dx = xpos - mx;
                dy = ypos - my;

                mx = xpos;
                my = ypos;
            }
        });
        window.registerMouseScrollCallback(new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scroll = yoffset;
            }
        });
    }

}
