package com.jgfx.gui.components.common;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.enums.Alignment;
import lombok.Getter;
import lombok.Setter;

/**
 * A generic alignment that can be applied to anything
 */
public class AlignmentCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private Alignment alignment = Alignment.LEADING;

    public AlignmentCmp(AbstractElement parent) {
        super(parent);
    }

    /**
     * @return returns the alignment
     */
    @Override
    public String dataString() {
        return "\"alignment\": \"" + alignment.name().toLowerCase() + "\"";
    }

}
