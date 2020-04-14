package com.jgfx.gui.events;

import com.jgfx.gui.elements.IElement;

public class ElementUpdateEvent {
    public final IElement<?> element;

    public ElementUpdateEvent(IElement<?> element) {
        this.element = element;
    }
}
