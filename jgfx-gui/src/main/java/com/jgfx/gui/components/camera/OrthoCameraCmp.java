package com.jgfx.gui.components.camera;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.window.IWindow;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents an orthographic camera
 */
public class OrthoCameraCmp implements Component {
    private final Matrix4f projection = new Matrix4f().identity();

    /**
     * Computes the projection
     *
     * @return returns the computed projection
     */
    public Matrix4f projection() {
        var window = CoreContext.get(IWindow.class);
//        return projection.identity().ortho2D(-window.getFbWidth() / 2.0f, window.getFbWidth() / 2.0f, window.getFbHeight() / 2.0f, -window.getFbHeight() / 2.0f);
        return projection.identity().ortho2D(0, window.getFbWidth(), window.getFbHeight(), 0);
    }

    /**
     * @return returns the projection matrix applied to the given matrix
     */
    public Matrix4f apply(Matrix4f other) {
        return projection().mul(other);
    }

}
