package com.jgfx.gui.helpers;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A helper class that does various things with consumers
 */
public final class Consumers {
    private static final Logger logger = LogManager.getLogger(Consumers.class);

    private Consumers() {
    }

    /**
     * This will run the consumer method and return an atomic reference to the value
     */
    public static <T extends AbstractElementCmp> AtomicReference<T> consume(AbstractElement element, Class<T> type, ComponentConsumer<T> consumer) {
        var atomic = new AtomicReference<T>();
        if (element.has(type)) {
            if (element.component(type) != null) {
                var component = type.cast(element.component(type));
                if (component != null) {
                    atomic.set(component);
                    consumer.accept(component);
                } else {
                    T cmp = null;
                    try {
                        cmp = type.getConstructor(AbstractElement.class).newInstance(element);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    consumer.accept(cmp);
                    atomic.set(cmp);
                    assert cmp != null;
                    element.add(cmp);
                }
            }
        } else {
            T cmp = null;
            try {
                cmp = type.getConstructor(AbstractElement.class).newInstance(element);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            consumer.accept(cmp);
            atomic.set(cmp);
            assert cmp != null;
            element.add(cmp);
            logger.warn("Component {}, was not found for the element {}, created new instance...", type.getSimpleName(), element.name());
        }
        return atomic;
    }

}
