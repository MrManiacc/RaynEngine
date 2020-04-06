package com.jgfx.engine.utils;

import lombok.Getter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * Useful class for updating OpenGL state, such as alpha blending, depth testing, etc.
 *
 * @author Karl
 */
public class GLUtils {
    @Getter
    private boolean cullingBackFace = false;
    @Getter
    private boolean wireframe = false;
    @Getter
    private boolean alphaBlending = false;
    @Getter
    private boolean additiveBlending = false;
    @Getter
    private boolean antialiasing = false;
    @Getter
    private boolean depthTesting = false;

    /**
     * Enable or disable antialiasing
     */
    public void antialias(boolean enable) {
        if (enable && !antialiasing) {
            GL11.glEnable(GL13.GL_MULTISAMPLE);
            antialiasing = true;
        } else if (!enable && antialiasing) {
            GL11.glDisable(GL13.GL_MULTISAMPLE);
            antialiasing = false;
        }
    }

    /**
     * Enable or disable alpha blending
     */
    public void alphaBlending(boolean enabled) {
        if (enabled && !alphaBlending) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            alphaBlending = true;
            additiveBlending = false;
        } else if (!enabled && alphaBlending) {
            GL11.glDisable(GL11.GL_BLEND);
            alphaBlending = false;
            additiveBlending = false;
        }
    }

    /**
     * Enables or disables alpha blending
     */
    public void additiveBlending(boolean enabled) {
        if (enabled && !additiveBlending) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            additiveBlending = true;
            alphaBlending = false;
        } else if (!enabled && additiveBlending) {
            GL11.glDisable(GL11.GL_BLEND);
            alphaBlending = false;
            additiveBlending = false;
        }
    }

    /**
     * Enables or disables depth testing
     */
    public void depthTest(boolean enable) {
        if (enable && !depthTesting) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            depthTesting = true;
        } else if (!enable && depthTesting) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            depthTesting = false;
        }
    }

    /**
     * Enables or disables back face culling
     */
    public void cullBackFaces(boolean enabled) {
        if (enabled && !cullingBackFace) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
            cullingBackFace = true;
        } else if (!enabled && cullingBackFace) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            cullingBackFace = false;
        }
    }

    /**
     * Enables or disables wireframe
     */
    public void wireframe(boolean enabled) {
        if (enabled && !wireframe) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            wireframe = true;
        } else if (!enabled && wireframe) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            wireframe = false;
        }
    }

    /**
     * Clears the depth(if enabled) and the color (if enabled
     */
    public void clear(boolean depth, boolean color) {
        if (depth && color)
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        else if (depth)
            glClear(GL_DEPTH_BUFFER_BIT);
        else if (color)
            glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Clears the screen with the given color
     */
    public void color(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

}