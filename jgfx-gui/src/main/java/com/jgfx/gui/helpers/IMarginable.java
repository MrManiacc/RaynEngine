package com.jgfx.gui.helpers;

import com.jgfx.gui.components.common.MarginCmp;
import com.jgfx.gui.components.common.PaddingCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;

/**
 * Helpers methods for margins
 */
public interface IMarginable<T extends AbstractElement> extends IElement<T> {

    /**
     * Sets the margins of the element
     */
    default T margin(int left, int top, int right, int bottom) {
        get(MarginCmp.class).ifPresentOrElse(marginCmp -> {
            if (left != Integer.MIN_VALUE)
                marginCmp.setLeft(left);
            if (top != Integer.MIN_VALUE)
                marginCmp.setTop(top);
            if (right != Integer.MIN_VALUE)
                marginCmp.setRight(right);
            if (bottom != Integer.MIN_VALUE)
                marginCmp.setBottom(bottom);
        }, () -> {
            var marginCmp = new MarginCmp(cast());
            if (left != Integer.MIN_VALUE)
                marginCmp.setLeft(left);
            if (top != Integer.MIN_VALUE)
                marginCmp.setTop(top);
            if (right != Integer.MIN_VALUE)
                marginCmp.setRight(right);
            if (bottom != Integer.MIN_VALUE)
                marginCmp.setBottom(bottom);
            add(marginCmp);
        });
        return cast();
    }

    /**
     * Sets the left margin
     */
    default T marginLeft(int left) {
        return margin(left, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Sets the top margin
     */
    default T marginTop(int top) {
        return margin(Integer.MAX_VALUE, top, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Sets the right margin
     */
    default T marginRight(int right) {
        return margin(Integer.MAX_VALUE, Integer.MAX_VALUE, right, Integer.MIN_VALUE);
    }

    /**
     * Sets the bottom margin
     */
    default T marginBottom(int bottom) {
        return margin(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, bottom);
    }

}
