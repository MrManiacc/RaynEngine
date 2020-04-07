package com.jgfx.gui.components;

import com.jgfx.engine.ecs.component.Component;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

/**
 * This is a simple component that stores the position and size of a rect
 */
public class RectCmp implements Component {
    public float x, y, width, height;
    private final Matrix4f modelMatrix = new Matrix4f();

    /**
     * @return returns the computed model matrix for the rect
     */
    public Matrix4f modelMatrix() {
        return this.modelMatrix.identity().translate(x, y, 0).scale(1);
    }

}
