package com.jgfx.gui.elements.containers;

import com.jgfx.gui.components.common.AlignmentCmp;
import com.jgfx.gui.components.container.ContainerCmp;
import com.jgfx.gui.components.display.size.BoundsCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.enums.Alignment;
import com.jgfx.gui.exception.GuiException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This allows for an element to have child elements
 */
public interface IContainer<T extends AbstractElement> extends IElement<T> {

    /**
     * Adds a child to the container
     */
    void add(IElement element);

    /**
     * Adds a child to the container
     */
    void add(int index, IElement element);

    /**
     * Removes a child from the container
     */
    void remove(IElement element);

    /**
     * Removes a child from the container
     */
    void remove(int index);

    /**
     * Removes all elements with the given type
     */
    void removeAll(Class<? extends IElement> elementType);

    /**
     * @return returns the number of elements with the given type
     */
    int count(Class<? extends IElement> elementType);

    /**
     * @return returns the total number of elements
     */
    int count();

    /**
     * @return returns a collection of the elements with the given type
     */
    <U extends IElement> List<U> list(Class<U> elementType);

    /**
     * Lists all of the elements
     */
    List<IElement> list();

    /**
     * This will sort the list based on y position
     */
    default void sort() {
        Collections.sort(list());
    }

    /**
     * This will attempt to find the index of the element in the container
     */
    default int findIndex(IElement<?> match) {
        var list = list();
        for (int i = 0; i < list.size(); i++) {
            var element = list.get(i);
            if (element.equals(match))
                return i;
        }
        return -1;
    }

    /**
     * @return the element of type at the given index
     */
    default <U extends IElement> Optional<U> element(int index, Class<U> elementType) {
        return Optional.ofNullable(list(elementType).get(index));
    }


    /**
     * @return the element of type at the first index
     */
    default <U extends IElement> Optional<U> element(Class<U> elementType) {
        return element(0, elementType);
    }

    /**
     * With is the main method of adding elements to a stack.
     * It takes in a supplier an optional position for the position in the stack,
     * and a required Supplier<Element> which is where you’ll specify the actual element to add to the stack.
     */
    default T with(int order, Supplier<IElement> supplier) {
        var child = supplier.get();
        child.setChild(true);
        add(order, child);
        return cast();
    }

    /**
     * With is the main method of adding elements to a stack.
     * It takes in a supplier an optional position for the position in the stack,
     * and a required Supplier<Element> which is where you’ll specify the actual element to add to the stack.
     */
    default T with(Supplier<IElement> supplier) {
        var child = supplier.get();
        child.setChild(true);
        add(child);
        return cast();
    }

    /**
     * This will add of the elements
     */
    default T with(IElement... elements) {
        for (var element : elements) {
            add(element);
            element.setChild(true);
        }
        return cast();
    }

    /**
     * Allows for multiple suppliers
     */
    default T with(Supplier<IElement>... suppliers) {
        for (var supplier : suppliers) {
            var element = supplier.get();
            add(element);
            logger().debug("Adding element {} to stack {}", element.name(), name());
        }
        return cast();
    }

    /**
     * Here we're checking to see if the container has an alignment and if it
     * doesn't we automatically add a center alignment
     */
    default void preBuild() throws GuiException {
        if (!has(AlignmentCmp.class)) {
            //We add center alignment if it's not present
            var alignment = new AlignmentCmp(cast());
            alignment.setAlignment(Alignment.CENTER);
            add(alignment);
            logger().debug("{} was found without an alignment automatically added a center alignment", name());
        }
        add(new ContainerCmp(cast()));
        IElement.super.preBuild();
    }
}
