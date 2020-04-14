package com.jgfx.gui.helpers;

import com.jgfx.assets.naming.Name;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.gui.components.image.ImageCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.elements.controls.Image;

/**
 * A helper class for adding various images
 */
public interface ITexturable<T extends AbstractElement> extends IElement<T> {
    /**
     * Sets the background image for an element
     */
    default T background(ResourceUrn urn) {
        get(ImageCmp.class).ifPresentOrElse(bgCmp -> {
            var texture = Assets.get(urn, Texture.class);
            if (texture.isEmpty())
                logger().warn("Failed to find texture {} for element {}", urn, name());
            else
                bgCmp.setTexture(texture.get());
        }, () -> {
            var bgCmp = new ImageCmp(cast());
            var texture = Assets.get(urn, Texture.class);
            if (texture.isEmpty())
                logger().warn("Failed to find texture {} for element {}", urn, name());
            else
                bgCmp.setTexture(texture.get());
            add(bgCmp);
        });
        return cast();
    }

    /**
     * Sets the background texture to the texture with the given name
     */
    default T background(Name name) {
        return background(new ResourceUrn(new Name("engine"), new Name("textures"), name));
    }

}
