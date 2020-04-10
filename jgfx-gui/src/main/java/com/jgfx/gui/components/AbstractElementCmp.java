package com.jgfx.gui.components;

import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import lombok.Getter;

/**
 * This is a generic element component, it stores some data about
 * the given element
 */
public abstract class ElementCmp implements Component {
    @Getter private EntityRef parent;

    public ElementCmp(EntityRef parent) {
        this.parent = parent;
    }
}
