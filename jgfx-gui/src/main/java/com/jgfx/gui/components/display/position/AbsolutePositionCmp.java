package com.jgfx.gui.components.display.position;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

/**
 * The position of a component, which will be computed at runtime
 */
public class AbsolutePositionCmp extends AbstractPositionCmp {
    public AbsolutePositionCmp(AbstractElement element) {
        super(element);
    }
}
