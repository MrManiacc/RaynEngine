package com.jgfx.gui.elements.stack;

import com.google.common.collect.Lists;
import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.elements.IContainer;
import com.jgfx.gui.exception.GuiException;

import java.util.ArrayList;
import java.util.List;

/**
 * This is used to simplify and abstract the way we interact with stacks. It decouples by using the IContainable
 */
public abstract class AbstractStack<T extends AbstractElement> extends AbstractElement<T> implements IContainer<T> {
    private List<IElement> elements;

    public AbstractStack(Class<T> type) {
        super(type);
        elements = Lists.newArrayList();
    }

    /**
     * Adds a child to the container
     */
    public void add(IElement element) {
        elements.add(element);
        element.setParent(this);
        logger.debug("Added element {} to stack {} at index {}", element.name(), name(), elements.size() - 1);

    }

    /**
     * Adds a child to the container
     */
    public void add(int index, IElement element) {
        if (index >= 0) {
            if (index > elements.size()) {
                List<IElement> newList = Lists.newArrayListWithCapacity(index + 1);
                newList.addAll(elements);
                this.elements = newList;
                logger.debug("Attempted to add {} to {} at index {}, but was out of bounds so we resized", element.name(), name(), index);
            }
            elements.add(index, element);
            element.setParent(this);
            logger.debug("Added element {} to stack {} at index {}", element.name(), name(), index);
        }
    }

    /**
     * Removes a child from the container
     */
    public void remove(IElement element) {
        elements.remove(element);
    }

    /**
     * Removes a child from the container
     */
    public void remove(int index) {
        if (index >= 0 && index < elements.size())
            elements.remove(index);
    }

    /**
     * Removes all elements with the given type
     */
    public void removeAll(Class<? extends IElement> elementType) {
        elements.stream().filter(element -> element.getClass().isAssignableFrom(elementType)).forEach(this::remove);
    }

    /**
     * @return returns the number of elements with the given type
     */
    public int count(Class<? extends IElement> elementType) {
        return (int) elements.stream().filter(element -> element.getClass().isAssignableFrom(elementType)).count();
    }

    /**
     * This will cast all of the objects to the given type, so dont worry ab it bro
     *
     * @return returns a collection of the elements with the given type
     */
    public <U extends IElement> List<U> list(Class<U> elementType) {
        List<U> newList = Lists.newArrayList();
        elements.stream().filter(element -> element.getClass().isAssignableFrom(elementType)).forEach(iElement -> {
            newList.add(elementType.cast(iElement));
        });
        return newList;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("{").append("\"").append("root").append("\"").append(":").append("{");
        sb.append("\"").append("element").append("\"").append(":").append("\"").append(name()).append("\"").append(",");
        appendElementComponents(this, sb, true);
        sb.append(",");
        appendElements(sb);
        sb.append("}").append("}");
        return formatJson(sb.toString());
//        return sb.toString();
    }

    /**
     * Appends the elements to the string builder
     */
    private void appendElements(StringBuilder sb) {
        if (!elements.isEmpty()) {
            sb.append("\"elements\":").append("[");
            for (int i = 0; i < elements.size(); i++) {
                var element = elements.get(i);
                var name = element.name(false);
                sb.append("{");
                {
                    //Append the name
                    sb.append("\"type\"")
                            .append(":")
                            .append("\"")
                            .append(name)
                            .append("\"");
                    appendElementComponents(element, sb, false);
                    if (element instanceof AbstractStack) {
                        sb.append(",");
                        ((AbstractStack) element).appendElements(sb);
                    }
                }
                sb.append("}");
                if (i != elements.size() - 1)
                    sb.append(",");
            }
            sb.append("]");
        }
    }

    /**
     * Appends the component data for a given element
     */
    private void appendElementComponents(IElement element, StringBuilder sb, boolean isSelf) {
        var components = new ArrayList<AbstractElementCmp>(element.components());
        if (!isSelf)
            sb.append(",");
        sb.append("\"").append("components").append("\"").append(":").append("[");
        {
            for (var i = 0; i < components.size(); i++) {
                var cmp = components.get(i);
                appendComponent(cmp, sb);
                if (i != components.size() - 1)
                    sb.append(",");
            }
        }
        sb.append("]");
    }

    /**
     * Appends a component to the component data
     */
    private void appendComponent(AbstractElementCmp component, StringBuilder sb) {
        sb.append("{");
        {
            sb.append("\"").append("type").append("\"").append(":");
            sb.append("\"").append(component.name()).append("\"").append(",");
            sb.append(component.dataString());
        }
        sb.append("}");
    }

    /**
     * We want to build all of the children entities when building
     */
    @Override
    public T build() throws GuiException {
        for (IElement element : elements)
            element.build();
        return super.build();
    }
}
