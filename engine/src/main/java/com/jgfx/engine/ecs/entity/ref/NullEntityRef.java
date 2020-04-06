package com.jgfx.engine.ecs.entity.ref;


import com.jgfx.engine.ecs.entity.pool.EntityPool;

/**
 * Null entity implementation - acts the same as an empty entity, except you cannot add anything to it.
 */
public final class NullEntityRef extends EntityRef {
    private NullEntityRef() {
    }

    @Override
    public EntityRef copy() {
        return this;
    }

    @Override
    public boolean isExists() {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void dispose() {
        /*Do nothing*/
    }

    /**
     * Null entities will always have an id of -1
     *
     * @return returns -1
     */
    @Override
    public long getId() {
        return -1;
    }

    @Override
    public String toJson() {
        return "EntityRef:{}";
    }

    @Override
    protected EntityPool getPool() {
        return null;
    }
}
