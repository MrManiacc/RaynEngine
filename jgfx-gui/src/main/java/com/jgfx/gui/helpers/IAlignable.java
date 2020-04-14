package com.jgfx.gui.helpers;

import com.jgfx.gui.components.common.AlignmentCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.enums.Alignment;

/**
 * Helpers methods for alignment
 */
public interface IAlignable<T extends AbstractElement> extends IElement<T> {

    /**
     * Sets the alignment for the text element
     */
    default T align(Alignment alignment) {
//
//        Consumers.consume(cast(), AlignmentCmp.class, component -> {
//            component.setAlignment(alignment);
//        });

        componentEdit(AlignmentCmp.class, component -> component.setAlignment(alignment));


//        get(AlignmentCmp.class).ifPresentOrElse(alignmentCmp -> alignmentCmp.setAlignment(alignment), () -> {
//            var alignmentCmp = new AlignmentCmp(cast());
//            alignmentCmp.setAlignment(alignment);
//            add(alignmentCmp);
//        });
        return cast();
    }

    /**
     * Aligns to the bottom center
     */
    default T bottom() {
        return align(Alignment.BOTTOM);
    }

    /**
     * Aligns to bottom leading
     */
    default T bottomLeading() {
        return align(Alignment.BOTTOM_LEADING);
    }

    /**
     * Aligns to bottom leading
     */
    default T bottomTrailing() {
        return align(Alignment.BOTTOM_TRAILING);
    }

    /**
     * Aligns to the bottom center
     */
    default T center() {
        return align(Alignment.CENTER);
    }

    /**
     * Aligns to center leading
     */
    default T leading() {
        return align(Alignment.LEADING);
    }

    /**
     * Aligns to center trailing
     */
    default T trailing() {
        return align(Alignment.TRAILING);
    }

    /**
     * Aligns to the top center
     */
    default T top() {
        return align(Alignment.TOP);
    }

    /**
     * Aligns to bottom leading
     */
    default T topLeading() {
        return align(Alignment.TOP_LEADING);
    }

    /**
     * Aligns to bottom leading
     */
    default T topTrailing() {
        return align(Alignment.TOP_TRAILING);
    }
}
