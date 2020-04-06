package com.jgfx.engine.ecs.entity.util;

import com.jgfx.engine.ecs.entity.pool.EntityPool;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import gnu.trove.iterator.TLongIterator;

import java.util.Iterator;


/**
 * Provides an iterator over EntityRefs, after being given an iterator over entity IDs.
 */
public class EntityIterator implements Iterator<EntityRef> {
    private TLongIterator idIterator;
    private EntityPool pool;

    public EntityIterator(TLongIterator idIterator, EntityPool pool) {
        this.idIterator = idIterator;
        this.pool = pool;
    }

    @Override
    public boolean hasNext() {
        return idIterator.hasNext();
    }

    @Override
    public EntityRef next() {
        return pool.getEntity(idIterator.next());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
