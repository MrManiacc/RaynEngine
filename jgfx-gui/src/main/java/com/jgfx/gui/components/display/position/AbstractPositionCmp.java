package com.jgfx.gui.components.display.position;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractPositionCmp extends AbstractElementCmp {
    @Getter
    @Setter
    protected float x = 0, y = 0;

    public AbstractPositionCmp(AbstractElement element) {
        super(element);
    }

    @Override
    public String dataString() {
        return "\"x\": " + x + "," + "\"y\": " + y;
    }

}
