package com.jgfx.engine.physics.events;

import com.badlogic.gdx.physics.box2d.Contact;
import com.jgfx.engine.ecs.entity.ref.EntityRef;

/**
 * A generic contact event posted by the pong physics system
 */
public interface ContactEvent {
    EntityRef getEntityA();

    EntityRef getEntityB();

    Contact getContact();
}
