package com.jgfx.gui.components.text;

import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Simply stores some text, nothing more
 */
public class TextCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private String text = "";

    public TextCmp(AbstractElement element) {
        super(element);
    }

    @Override
    public String dataString() {
        return "\"text\": \"" + text + "\"";
    }
}
