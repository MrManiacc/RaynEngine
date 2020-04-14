package com.jgfx.gui.helpers;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.components.display.size.BoundsCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.SneakyThrows;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@FunctionalInterface
public interface ComponentConsumer<T extends AbstractElementCmp> extends Consumer<T> {
    @Override
    void accept(T component);

}
