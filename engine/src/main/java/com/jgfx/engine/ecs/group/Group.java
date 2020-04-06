package com.jgfx.engine.ecs.group;


import com.jgfx.engine.ecs.entity.ref.EntityRef;

import java.util.Iterator;

/**
 * Creates a group of entities based on the given classes
 */
public class Group implements Iterable<EntityRef> {
    private final Iterable<EntityRef> parent;

    Group(Iterable<EntityRef> iterable) {
        this.parent = iterable;
    }

    @Override
    public Iterator<EntityRef> iterator() {
        return parent.iterator();
    }


}


