package com.jgfx.gui.components.common;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a padding component
 */
public class MarginCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private int left = 5, right = 5, top = 5, bottom = 5;

    public MarginCmp(AbstractElement element) {
        super(element);
    }

    @Override
    public String dataString() {
        return "\"left\": " + left + "," +
                "\"top\": " + top + "," +
                "\"right\": " + right + "," +
                "\"bottom\": " + bottom;
    }
}
