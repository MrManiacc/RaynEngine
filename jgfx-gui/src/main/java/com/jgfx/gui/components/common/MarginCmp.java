package com.jgfx.gui.components.common;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a padding component
 */
public class PaddingCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private float left = 5.0f, right = 5.0f, top = 5.0f, bottom = 5.0f;

    public PaddingCmp(AbstractElement element) {
        super(element);
    }

    @Override
    protected String dataString() {
        return "'left': " + left + ",\n" +
                "'top': " + top + ",\n" +
                "'right': " + right + ",\n" +
                "'bottom': " + bottom;
    }
}
