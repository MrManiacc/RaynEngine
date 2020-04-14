package com.jgfx.gui.components.container;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

public class SpacingCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private float spacing = 10.0f;

    public SpacingCmp(AbstractElement element) {
        super(element);
    }

    @Override
    public String dataString() {
        return "\"spacing\": " + spacing;
    }
}
