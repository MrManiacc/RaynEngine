package com.jgfx.gui.elements.controls;

import com.jgfx.assets.naming.Name;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.gui.components.color.BackgroundColorCmp;
import com.jgfx.gui.components.color.OverlayColorCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.enums.Colors;
import com.jgfx.gui.helpers.*;
import lombok.SneakyThrows;
import org.lwjgl.system.CallbackI;

/**
 * Represents an image
 */
public class Image extends AbstractElement<Image> implements ITexturable<Image>, IAlignable<Image>, IPaddable<Image>, IMarginable<Image>, IRenderable<Image> {

    @SneakyThrows
    public Image(ResourceUrn image) {
        super(Image.class);
        background(image);
        model(new Name("quad"));
    }

    @SneakyThrows
    public Image(Name image) {
        super(Image.class);
        background(image);
        model(new Name("quad"));
    }

    @SneakyThrows
    public Image(String image) {
        super(Image.class);
        if (ResourceUrn.isValid(image))
            background(new ResourceUrn(image));
        else
            background(new Name(image));
        model(new Name("quad"));
    }


    /**
     * Adds a color overlay on the image
     */
    public Image overlay(int r, int g, int b, int a) {
        componentEdit(OverlayColorCmp.class, overlay -> {
            overlay.setR(r);
            overlay.setG(g);
            overlay.setB(b);
            overlay.setA(a);
        });
        return this;
    }

    /**
     * Adds a color overlay with the given alpha value and hex color
     */
    public Image overlay(String colorStr, int alpha) {
        int r, g, b;
        if (colorStr.startsWith("#")) {
            r = Integer.valueOf(colorStr.substring(1, 3), 16);
            g = Integer.valueOf(colorStr.substring(3, 5), 16);
            b = Integer.valueOf(colorStr.substring(5, 7), 16);
        } else {
            r = Integer.valueOf(colorStr.substring(0, 2), 16);
            g = Integer.valueOf(colorStr.substring(2, 4), 16);
            b = Integer.valueOf(colorStr.substring(4, 6), 16);
        }
        return overlay(r, g, b, alpha);
    }

    /**
     * Adds a color overlay with the given alpha value and hex color
     */
    public Image overlay(Colors color, int alpha) {
        return overlay(color.getHex(), alpha);
    }

}
