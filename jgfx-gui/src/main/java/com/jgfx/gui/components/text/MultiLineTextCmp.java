package com.jgfx.gui.components.text;

import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.gui.components.AbstractElementCmp;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores the height of a text element
 */
public class LineHeightCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private float lineHeight;

    public LineHeightCmp(EntityRef parent, float lineHeight) {
        super(parent);
        this.lineHeight = lineHeight;
    }

}
