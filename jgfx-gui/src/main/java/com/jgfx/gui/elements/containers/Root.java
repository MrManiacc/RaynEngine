package com.jgfx.gui.elements.containers;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.event.Bus;
import com.jgfx.engine.window.IWindow;
import com.jgfx.gui.components.display.size.BoundsCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.events.ElementUpdateEvent;
import com.jgfx.gui.exception.GuiException;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

/**
 * The root container container, there will only be one instance
 */
public class Root extends AbstractElement<Root> implements IContainer<Root> {
    private final List<IElement> elements;
    private static boolean created = false;

    public Root() throws GuiException {
        super(Root.class);
        if (created)
            throw new GuiException("Attempted to create the root element twice!");
        elements = new ArrayList<>();
        created = true;
    }

    @Override
    public void add(IElement element) {
        elements.add(element);
    }

    @Override
    public void add(int index, IElement element) {
        elements.add(index, element);
    }

    @Override
    public void remove(IElement element) {
        elements.remove(element);
    }

    @SneakyThrows
    @Override
    public void remove(int index) {
        elements.remove(index);
    }

    @Override
    public void removeAll(Class<? extends IElement> elementType) {
        elements.removeIf(elementType::isInstance);
    }

    @Override
    public int count(Class<? extends IElement> elementType) {
        var count = 0;
        for (var element : elements)
            if (elementType.isInstance(element))
                count++;
        return count;
    }

    public int count() {
        return elements.size();
    }


    @Override
    public <U extends IElement> List<U> list(Class<U> elementType) {
        var list = new ArrayList<U>();
        for (var element : elements)
            if (elementType.isInstance(element))
                list.add(elementType.cast(element));
        return list;
    }

    public List<IElement> list() {
        return elements;
    }

    /**
     * Returns the computed bounds
     */
    public BoundsCmp getBounds() {
        var window = CoreContext.get(IWindow.class);
        get(BoundsCmp.class).ifPresentOrElse(bounds -> {
            bounds.setPosition(0, 0);
            bounds.setSize(window.getWidth(), window.getHeight());
        }, () -> {
            var bounds = new BoundsCmp(this);
            bounds.setPosition(0, 0);
            bounds.setSize(window.getWidth(), window.getHeight());
            add(bounds);
        });
        return get(BoundsCmp.class).get();
    }


    /**
     * The root doesn't have any components, so we just build the children
     */
    @Override
    public Root build() throws GuiException {
        for (IElement element : elements)
            element.build();
        super.build();
        Bus.GUI.post(new ElementUpdateEvent(this));
        return this;
    }

}
