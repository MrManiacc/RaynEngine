package com.jgfx.gui.components;

import com.jgfx.engine.ecs.entity.ref.EntityRef;
import lombok.Getter;
import lombok.Setter;

/**
 * Simply stores some text, nothing more
 */
public class TextCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private String text;


    public TextCmp(String text, EntityRef parent) {
        super(parent);
        this.text = text;
    }
}
