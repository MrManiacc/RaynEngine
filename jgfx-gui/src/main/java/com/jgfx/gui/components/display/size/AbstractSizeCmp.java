package com.jgfx.gui.components.display.size;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractSizeCmp extends AbstractElementCmp {
    @Getter
    @Setter
    protected float width, height;

    public AbstractSizeCmp(AbstractElement element) {
        super(element);
    }

    @Override
    public String dataString() {
        return "\"width\": " + width + "," + "\"height\": " + height;
    }
}
