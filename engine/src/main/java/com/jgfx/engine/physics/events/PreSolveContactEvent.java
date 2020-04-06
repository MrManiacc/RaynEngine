package com.jgfx.engine.physics.events;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.physics.exceptions.InvalidContactException;
import lombok.Getter;
import lombok.SneakyThrows;

public class PreSolveContactEvent implements ContactEvent {
    @Getter private EntityRef entityA;
    @Getter private EntityRef entityB;
    @Getter private Contact contact;
    @Getter private Manifold oldManifold;

    @SneakyThrows
    public PreSolveContactEvent(Contact contact, Manifold oldManifold) {
        this.contact = contact;
        if (!(contact.getFixtureA().getUserData() instanceof EntityRef) || !(contact.getFixtureB().getUserData() instanceof EntityRef))
            throw new InvalidContactException();

        this.entityA = (EntityRef) contact.getFixtureA().getUserData();
        this.entityB = (EntityRef) contact.getFixtureB().getUserData();
        this.oldManifold = oldManifold;
    }
}
