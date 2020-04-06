package com.jgfx.load;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.components.CameraComponent;
import com.jgfx.components.MaterialComponent;
import com.jgfx.components.Physics2dComponent;
import com.jgfx.engine.ecs.World;
import com.jgfx.engine.ecs.component.SingleComponent;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.load.SingleStepLoadProcess;
import com.jgfx.engine.window.IWindow;

import java.util.Optional;

@AutoRegister
public class GameEntitiesLoad extends SingleStepLoadProcess {
    @In private World world;
    @In private com.badlogic.gdx.physics.box2d.World physicsWorld;
    @In private IWindow window;

    public GameEntitiesLoad() {
        super("Loading default entities...");
    }

    @Override
    public boolean step() {
        createBall(0, 0);
        createControllers();
        createArena();
        return true;
    }

    /**
     * Creates the ball entity
     */
    private void createBall(float x, float y) {
        //Load the player entity
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the worlds
        bodyDef.angularVelocity = 5;
        bodyDef.position.set(x, y);
        bodyDef.linearVelocity.x = (((float) Math.random() * 2) - 1) * 10000;
        bodyDef.linearVelocity.y = (((float) Math.random() * 2) - 1) * 10000;
        bodyDef.bullet = true;
        // Create our body in the world using our body definition
        Body body = physicsWorld.createBody(bodyDef);
        // Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(1);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 5; // Make it bounce a little bit


        // Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(world.createEntity(
                new Physics2dComponent(body, fixture, bodyDef, circle),
                new MaterialComponent(new ResourceUrn("engine", "shapes", "quad"), new ResourceUrn("engine:textures#ball"))
        ));
    }

    /**
     * Creates the player controller and the ai controller
     */
    private void createControllers() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.2f, 2.8f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 10000;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0f; // Make it bounce a little bit
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 10f;
        bodyDef.position.x = 23;
        bodyDef.fixedRotation = true;

        //=========Load the player entity
        Body playerBody = physicsWorld.createBody(bodyDef);
        Fixture fixture = playerBody.createFixture(fixtureDef);
        fixture.setUserData(world.createEntity(
                new SingleComponent("engine:entities#local-player"),
                new CameraComponent((float) Math.toRadians(120), window.getWidth() / (float) window.getHeight(), 0.1f, 1000f),
                new Physics2dComponent(playerBody, fixture, bodyDef, shape),
                new MaterialComponent(new ResourceUrn("engine", "shapes", "quad"), new ResourceUrn("engine:textures#white"))
        ));

        //=========Load the opponent entity
        Body opponentBody = physicsWorld.createBody(bodyDef);
        opponentBody.setTransform(new Vector2(-23, 0), 0);
        Fixture opponentFixture = opponentBody.createFixture(fixtureDef);
        opponentFixture.setUserData(world.createEntity(
                new SingleComponent("engine:entities#opponent-player"),
                new Physics2dComponent(opponentBody, opponentFixture, bodyDef, shape),
                new MaterialComponent(new ResourceUrn("engine", "shapes", "quad"), new ResourceUrn("engine:textures#white"))
        ));
    }

    /**
     * Creates the area for the component
     */
    private void createArena() {
        createWall(0, -15, 25, 0.3f, Optional.empty());
        createWall(0, 15, 25, 0.3f, Optional.empty());
        createWall(25, 0, 0.3f, 15.3f, Optional.of(new SingleComponent("engine:entities#opponent-wall")));
        createWall(-25, 0, 0.3f, 15.3f, Optional.of(new SingleComponent("engine:entities#player-wall")));
    }

    /**
     * Creates a wall component
     *
     * @param x x pos
     * @param y y pos
     * @param w width
     * @param h height
     */
    private void createWall(float x, float y, float w, float h, Optional<SingleComponent> single) {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(x, y));
        Body groundBody = physicsWorld.createBody(groundBodyDef);
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(w, h);
        Fixture groundFixture = groundBody.createFixture(groundBox, 0);
        single.ifPresentOrElse(singleComponent -> {
            groundFixture.setUserData(world.createEntity(
                    singleComponent,
                    new Physics2dComponent(groundBody, groundFixture, groundBodyDef, groundBox),
                    new MaterialComponent("engine:shapes#quad", "engine:textures#white")
            ));
        }, () -> {
            groundFixture.setUserData(world.createEntity(
                    new Physics2dComponent(groundBody, groundFixture, groundBodyDef, groundBox),
                    new MaterialComponent("engine:shapes#quad", "engine:textures#white")
            ));
        });
    }
}
