package com.jgfx.gui.components.text;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.enums.Alignment;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores the height of a text element
 */
public class MultiLineTextCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private float lineHeight = 1.0f, lineSpacing = 0.1f;

    @Getter
    @Setter
    private Alignment textAlignment = Alignment.LEADING;

    public MultiLineTextCmp(AbstractElement element) {
        super(element);
    }


    @Override
    public String dataString() {
        return "\"lineHeight\": " + lineHeight + "," +
                "\"lingSpacing\": " + lineSpacing + "," +
                "\"alignment\": " + textAlignment.name().toLowerCase();
    }
}
