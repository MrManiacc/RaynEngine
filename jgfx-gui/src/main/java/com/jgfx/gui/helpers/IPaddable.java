package com.jgfx.gui.helpers;

import com.jgfx.gui.components.common.MarginCmp;
import com.jgfx.gui.components.common.PaddingCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;

/**
 * Helpers methods for padding
 */
public interface IPaddable<T extends AbstractElement> extends IElement<T> {

    /**
     * Sets the padding of the element
     */
    default T padding(int left, int top, int right, int bottom) {
        get(PaddingCmp.class).ifPresentOrElse(paddingCmp -> {
            if (left != Integer.MIN_VALUE)
                paddingCmp.setLeft(left);
            if (top != Integer.MIN_VALUE)
                paddingCmp.setTop(top);
            if (right != Integer.MIN_VALUE)
                paddingCmp.setRight(right);
            if (bottom != Integer.MIN_VALUE)
                paddingCmp.setBottom(bottom);
        }, () -> {
            var paddingCmp = new PaddingCmp(cast());
            if (left != Integer.MIN_VALUE)
                paddingCmp.setLeft(left);
            if (top != Integer.MIN_VALUE)
                paddingCmp.setTop(top);
            if (right != Integer.MIN_VALUE)
                paddingCmp.setRight(right);
            if (bottom != Integer.MIN_VALUE)
                paddingCmp.setBottom(bottom);
            add(paddingCmp);
        });
        return cast();
    }

    /**
     * Sets the left padding
     */
    default T paddingLeft(int left) {
        return padding(left, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Sets the top padding
     */
    default T paddingTop(int top) {
        return padding(Integer.MAX_VALUE, top, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Sets the right padding
     */
    default T paddingRight(int right) {
        return padding(Integer.MAX_VALUE, Integer.MAX_VALUE, right, Integer.MIN_VALUE);
    }

    /**
     * Sets the bottom padding
     */
    default T paddingBottom(int bottom) {
        return padding(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, bottom);
    }
}
