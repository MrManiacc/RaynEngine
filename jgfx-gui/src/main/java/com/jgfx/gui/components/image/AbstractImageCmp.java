package com.jgfx.gui.components.image;

import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores a texture urn
 */
public class AbstractImage extends AbstractElementCmp {
    @Getter
    @Setter
    private ResourceUrn imageUrn;

    public AbstractImage(AbstractElement element) {
        super(element);
    }

    @Override
    protected String dataString() {
        return "\t\t\"image\": \"" + imageUrn.toString() + "\"";
    }
}
