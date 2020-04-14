package com.jgfx.gui.components;

import com.jgfx.engine.ecs.component.Component;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.helpers.INameable;

/**
 * This is a generic element component, it stores some data about
 * the given element
 */
public abstract class AbstractElementCmp implements Component, INameable {
    private IElement element;

    public AbstractElementCmp(AbstractElement element) {
        this.element = element;
    }


    /**
     * Not sure if this will work or not, but if it does it allows us to get the element type
     * from the component
     *
     * @return returns the element type
     */
    public <T extends AbstractElement> T element() {
        return (T) element.getType().cast(element);
    }


    /**
     * Ths will take the components parent and cast it to the given type
     *
     * @return returns the element type
     */
    public <U extends IElement<? extends AbstractElement>> U element(Class<U> as) {
        return (U) element.getType().cast(element);
    }

    /**
     * @return returns the data about the component as a string
     */
    public abstract String dataString();

    /**
     * @return returns a json string of the class
     */
    @Override
    public String toString() {
        return "\"" + name() + "\": {" +
                dataString() +
                "}";
    }


}
