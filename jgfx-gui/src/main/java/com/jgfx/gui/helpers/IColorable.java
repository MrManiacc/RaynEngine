package com.jgfx.gui.helpers;

import com.jgfx.gui.components.color.BackgroundColorCmp;
import com.jgfx.gui.components.color.ForegroundColorCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.enums.Colors;

/**
 * This interface adds methods for adding a foreground and background color to an element
 */
public interface IColorable<T extends AbstractElement> extends IElement<T> {

    /**
     * Sets the color component to the given color value
     */
    default T foreground(int r, int g, int b, int a) {
        get(ForegroundColorCmp.class).ifPresentOrElse(colorCmp -> {
            colorCmp.setR(r);
            colorCmp.setG(g);
            colorCmp.setB(b);
            colorCmp.setA(a);
        }, () -> {
            var colorCmp = new ForegroundColorCmp(cast());
            colorCmp.setR(r);
            colorCmp.setG(g);
            colorCmp.setB(b);
            colorCmp.setA(a);
            add(colorCmp);
        });
        return cast();
    }

    /**
     * Creates a color with a default alpha value of 255
     */
    default T foreground(int r, int g, int b) {
        return foreground(r, g, b, 255);
    }

    /**
     * Creates a background color with a default color value
     */
    default T foreground(Colors color) {
        return foreground(color.getHex());
    }

    /**
     * Creates a color with #
     */
    default T foreground(String colorStr) {
        return foreground(colorStr, 255);
    }

    /**
     * Creates a color with #
     */
    default T foreground(String colorStr, int alpha) {
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
        return foreground(r, g, b, alpha);
    }

    /**
     * Sets the color component to the given color value
     */
    default T background(int r, int g, int b, int a) {
        get(BackgroundColorCmp.class).ifPresentOrElse(colorCmp -> {
            colorCmp.setR(r);
            colorCmp.setG(g);
            colorCmp.setB(b);
            colorCmp.setA(a);
        }, () -> {
            var colorCmp = new BackgroundColorCmp(cast());
            colorCmp.setR(r);
            colorCmp.setG(g);
            colorCmp.setB(b);
            colorCmp.setA(a);
            add(colorCmp);
        });
        return cast();
    }

    /**
     * Creates a color with a default alpha value of 255
     */
    default T background(int r, int g, int b) {
        return background(r, g, b, 255);
    }

    /**
     * Creates a color with hex value
     */
    default T background(String hexColor, int alpha) {
        int r, g, b;
        if (hexColor.startsWith("#")) {
            r = Integer.valueOf(hexColor.substring(1, 3), 16);
            g = Integer.valueOf(hexColor.substring(3, 5), 16);
            b = Integer.valueOf(hexColor.substring(5, 7), 16);
        } else {
            r = Integer.valueOf(hexColor.substring(0, 2), 16);
            g = Integer.valueOf(hexColor.substring(2, 4), 16);
            b = Integer.valueOf(hexColor.substring(4, 6), 16);
        }
        return background(r, g, b, alpha);
    }

    /**
     * Creates a color with hex value
     */
    default T background(Colors color, int alpha) {
        return background(color.getHex(), alpha);
    }

    /**
     * Creates a color with hex value
     */
    default T background(String hexColor) {
        return background(hexColor, 255);
    }

    /**
     * Creates a background color with a default color value
     */
    default T background(Colors color) {
        return background(color.getHex());
    }
}
