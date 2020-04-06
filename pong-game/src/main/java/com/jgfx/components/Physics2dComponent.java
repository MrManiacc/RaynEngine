package com.jgfx.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.jgfx.engine.ecs.component.Component;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * A 2d physics component
 */
public class Physics2dComponent implements Component {
    public final Body body;
    public final Fixture fixture;
    public final BodyDef bodyDef;
    public final Shape shape;
    private final Matrix4f modelMatrix;
    private Vector2f scale;

    public Physics2dComponent(Body body, Fixture fixture, BodyDef bodyDef, Shape shape) {
        this.body = body;
        this.fixture = fixture;
        this.bodyDef = bodyDef;
        this.shape = shape;
        this.modelMatrix = new Matrix4f();
        if (shape instanceof CircleShape) {
            this.scale = new Vector2f(shape.getRadius());
        } else if (shape instanceof PolygonShape) {
            var poly = (PolygonShape) shape;
            Vector2 vert = new Vector2();
            poly.getVertex(2, vert);
            this.scale = new Vector2f(vert.x, vert.y);
        }
    }

    public Matrix4f getModelMatrix() {
        modelMatrix.identity().translate(body.getPosition().x, body.getPosition().y, -10f).scale(scale.x, scale.y, 1);
        return modelMatrix;
    }
}
