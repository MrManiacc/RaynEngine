package com.jgfx.gui.elements.helpers;

import com.jgfx.gui.components.color.BackgroundColorCmp;
import com.jgfx.gui.components.color.ForegroundColorCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.elements.controls.Text;

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
}
