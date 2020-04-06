package com.jgfx.player.data;

import com.jgfx.engine.ecs.component.Component;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents a transform in 3d space
 */
public class PlayerTransform implements Component {
    public float x, y, z;
    public float rx, ry;


    public PlayerTransform(Vector3f position, Vector3f rotation) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        this.rx = rotation.x;
        this.ry = rotation.y;
    }

    @Override
    public String toString() {
        return "Transform: {" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", rx=" + rx +
                ", ry=" + ry +
                '}';
    }
}
