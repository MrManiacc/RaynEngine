package com.jgfx.player.data;

import com.jgfx.engine.ecs.component.Component;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents a camera in 3d space
 */
public class PlayerCamera implements Component {
    public final Matrix4f projectionMatrix;
    public final Matrix4f viewMatrix;
    private final Vector3f forward, right, up;

    public PlayerCamera(float fov, float aspect, float near, float far) {
        this.projectionMatrix = new Matrix4f().perspective(fov, aspect, near, far);
        this.viewMatrix = new Matrix4f().translate(0, 0, 0).rotateX(0).rotateY(0);
        this.forward = new Vector3f();
        this.right = new Vector3f();
        this.up = new Vector3f();
    }

//    /**
//     * @return returns the transforms view matrix
//     */
//    public Matrix4f viewMatrix(PlayerTransform playerTransform) {
//        return viewMatrix.identity().translate(-playerTransform.x, -playerTransform.y, -playerTransform.z)
//                .rotateX((float) Math.toRadians(playerTransform.rx))
//                .rotateY((float) Math.toRadians(playerTransform.ry))
//                .scale(1.0f);
//    }

    /**
     * @return returns the unaltered view matrix
     */
    public Matrix4f viewMatrix() {
        return viewMatrix;
    }

    /**
     * @return returns the forward direction
     */
    public Vector3f forward() {
        return viewMatrix.positiveZ(forward).negate();
    }

    /**
     * @return returns the right direction
     */
    public Vector3f right() {
        return viewMatrix.positiveX(right);
    }

    /**
     * @return returns the up direction
     */
    public Vector3f up() {
        return viewMatrix.positiveY(up);
    }
}
