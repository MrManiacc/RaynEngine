package com.jgfx.gui.elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.containers.IContainer;
import com.jgfx.gui.enums.OutputType;
import com.jgfx.gui.exception.GuiException;
import com.jgfx.gui.helpers.ComponentConsumer;
import com.jgfx.gui.helpers.Consumers;
import com.jgfx.gui.helpers.INameable;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class exposes some methods that allows us to create other interfaces for some default style properties
 */
public interface IElement<T extends AbstractElement> extends INameable, Comparable<IElement> {
    /**
     * @return returns the component or an empty Optional if not present
     */
    <U extends AbstractElementCmp> Optional<U> get(Class<U> type);

    default <U extends Component> U component(Class<U> type) {
        return entity().get(type);
    }

    /**
     * @return returns true if the element has the given component type
     */
    <U extends AbstractElementCmp> boolean has(Class<U> type);

    /**
     * @return returns a collection of the current components
     */
    Collection<AbstractElementCmp> components();

    /**
     * @return this wil consume the consumer, or create a new component, and then apply the consumer.
     * Will be automatically added to the element and returned as an atomic reference
     */
    default <V extends AbstractElementCmp> AtomicReference<V> componentEdit(Class<V> type, ComponentConsumer<V> consumer) {
        return Consumers.consume(cast(), type, consumer);
    }

    /**
     * This will add the given component
     */
    boolean add(AbstractElementCmp component);

    /**
     * @return returns the type
     */
    Class<T> getType();

    /**
     * This method is what makes it all possible, and it's a beauty
     * it works because we force the user to statically type the call type during declaration
     *
     * @return returns a casted object in the form a abstract element
     */
    default T cast() {
        return getType().cast(this);
    }

    /**
     * @return returns true if this class is the type of element
     */
    default boolean is(Class<? extends IElement> type) {
        return type.isAssignableFrom(getType());
//        return getType().isAssignableFrom(type);//TODO: maybe reverse we'll see
    }

    /**
     * @return returns the entity
     */
    EntityRef entity();

    /**
     * @return returns the container object of this class, by casting
     */
    default IContainer container() {
        return container(false);
    }

    /**
     * @return this will find the root container, if root is set to true, other wise it will find the first level up
     */
    IContainer container(boolean root);

    /**
     * @return returns the logger
     */
    Logger logger();

    /**
     * Builds the element
     */
    T build() throws GuiException;

    /**
     * @return returns true if this is a child of something
     */
    boolean isChild();

    /**
     * Set the is child property
     */
    void setChild(boolean child);

    /**
     * Sets the parent container
     */
    void setParent(IContainer container);

    /**
     * @return returns the parent element or empty if it's not been set
     */
    IContainer getParent();

    /**
     * This will write a element to file for debugging
     */
    default IElement<T> toFile(File file) {
        toFile(file, OutputType.JSON);
        return this;
    }

    /**
     * This will write a element to file for debugging
     */
    default void toFile(File file, OutputType type) {
        try {
            FileUtils.writeStringToFile(file, toString(type), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return outputs the text as the given type
     */
    default String toString(OutputType type) {
        var mapper = new ObjectMapper();
        try {
            Object json = mapper.readValue(toString(), Object.class);
            if (type == OutputType.YAML) {
                var yamlWriter = new ObjectMapper(new YAMLFactory());
                return yamlWriter.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            } else if (type == OutputType.XML) {
                var xmlWriter = new XmlMapper();
                return xmlWriter.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return toString();
    }

    /**
     * This will be called right before the element is build so we can hook
     * it and add default components for things like containers
     */
    default void preBuild() throws GuiException {
    }
}
