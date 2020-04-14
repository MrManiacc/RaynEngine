package com.jgfx.gui.helpers;

import com.jgfx.gui.components.display.size.BoundsCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;

/**
 * This class will compute the correct position for a given element
 */
public interface ISizeComputable<T extends AbstractElement> extends IElement<T> {
    BoundsCmp computeBounds();
}
