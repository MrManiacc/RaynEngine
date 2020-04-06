package com.jgfx.engine.ecs.entity.ref;

import com.jgfx.engine.ecs.entity.pool.EntityPool;
import lombok.Getter;

public class BaseEntityRef extends EntityRef {
    @Getter
    private final long id;
    @Getter
    private final EntityPool pool;
    @Getter
    private boolean exists = true;
    @Getter
    private boolean active = true;

    public BaseEntityRef(EntityPool pool, long id) {
        this.pool = pool;
        this.id = id;
    }

    @Override
    public EntityRef copy() {
        return null;
    }

    @Override
    public void dispose() {
        if (isActive()) {
            exists = false;
            active = false;
            pool.destroy(id);
        }
    }

    @Override
    public String toJson() {
        return null;
    }

}
