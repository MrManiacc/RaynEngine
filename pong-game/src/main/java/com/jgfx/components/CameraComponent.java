package com.jgfx.components;

import com.jgfx.engine.ecs.component.Component;
import org.joml.Matrix4f;

/**
 * Represents a generic camera, which will be attached to an entity ref
 */
public class CameraComponent implements Component {
    public final Matrix4f projectionMatrix, viewMatrix;

    public CameraComponent(float fov, float aspect, float near, float far) {
        this.projectionMatrix = new Matrix4f().perspective(fov, aspect, near, far);
        this.viewMatrix = new Matrix4f().translate(0, 0, 0).rotateX(0).rotateY(0);
    }
}
