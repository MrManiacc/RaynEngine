package com.jgfx.gui.components.image;

import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores a texture urn
 */
public class ImageCmp extends AbstractElementCmp {
    /**
     * The asset it's self
     */
    @Getter
    @Setter
    private Texture texture;

    public ImageCmp(AbstractElement element) {
        super(element);
    }

    /**
     * @return redirects width
     */
    public float getWidth() {
        return texture.getWidth();
    }

    /**
     * @return redirects height
     */
    public float getHeight() {
        return texture.getHeight();
    }

    /**
     * @return returns the resource urn of the texture
     */
    public ResourceUrn getResourceUrn() {
        return texture.getUrn();
    }

    @Override
    public String dataString() {
        return "\"image\": \"" + texture.getUrn().toString() + "\"" + "," +
                "\"width\":" + getWidth() + "," +
                "\"height\":" + getHeight();
    }
}
