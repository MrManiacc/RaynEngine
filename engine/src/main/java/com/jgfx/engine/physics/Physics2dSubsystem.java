package com.jgfx.engine.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.event.Bus;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.physics.events.ContactBeginEvent;
import com.jgfx.engine.physics.events.ContactEndEvent;
import com.jgfx.engine.physics.events.PostSolveContactEvent;
import com.jgfx.engine.physics.events.PreSolveContactEvent;
import com.jgfx.engine.time.EngineTime;
import lombok.Getter;

import java.util.Objects;

public class Physics2dSubsystem implements ContactListener, EngineSubsystem {
    private World physicsWorld;
    @Getter private final String name = "Physics";
    @Getter private boolean loaded = false;

    /**
     * initialize this class as a contact listener
     */
    @Override
    public void preInitialise() {
        World.setVelocityThreshold(0f);
        physicsWorld = CoreContext.put(World.class, new World(new Vector2(0, 0), false));
        Objects.requireNonNull(physicsWorld).setContactListener(this);
        loaded = true;
    }

    /**
     * Update the physics world
     */
    @Override
    public void postUpdate() {
        physicsWorld.step(1.0f / 60.0f, 6, 6);
    }

    /**
     * Called at the start of a contact between two entities
     */
    @Override
    public void beginContact(Contact contact) {
        Bus.LOGIC.post(new ContactBeginEvent(contact));
    }

    /**
     * Called at the end of a contact between two entities
     */
    @Override
    public void endContact(Contact contact) {
        Bus.LOGIC.post(new ContactEndEvent(contact));
    }

    /**
     * Called before the collision is resolved
     */
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Bus.LOGIC.post(new PreSolveContactEvent(contact, oldManifold));
    }

    /**
     * Called after the collision is solved
     */
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Bus.LOGIC.post(new PostSolveContactEvent(contact, impulse));
    }

}
