package com.jgfx.lines;

import com.jgfx.engine.ecs.component.Component;
import lombok.Getter;
import org.joml.Vector2f;

/**
 * A line component that can be used to draw
 */
public class LineCmp implements Component {
    private Vector2f center;
    public LineCmp(Vector2f center, float length) {

    }

    public LineCmp(float x1, float y1, float x2, float y2) {
    }
}
