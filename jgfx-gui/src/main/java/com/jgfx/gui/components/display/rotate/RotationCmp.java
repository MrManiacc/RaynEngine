package com.jgfx.gui.components.display.rotate;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

public class RotationCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private float angle = 0;

    public RotationCmp(AbstractElement element) {
        super(element);
    }

    @Override
    public String dataString() {
        return "\"angle\":" + angle;
    }
}
