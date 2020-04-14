package com.jgfx.gui.elements;

import com.google.common.collect.Maps;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.ecs.World;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.ref.NullEntityRef;
import com.jgfx.engine.event.Bus;
import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.components.display.size.AbsoluteSizeCmp;
import com.jgfx.gui.components.display.size.BoundsCmp;
import com.jgfx.gui.elements.containers.IContainer;
import com.jgfx.gui.elements.containers.Root;
import com.jgfx.gui.events.ElementUpdateEvent;
import com.jgfx.gui.exception.GuiException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class represents a generic element.
 */
public abstract class AbstractElement<T extends AbstractElement> implements IElement<T> {
    //The entity of the element
    @Getter private EntityRef entity = NullEntityRef.NULL;
    private final Map<Class<? extends AbstractElementCmp>, AbstractElementCmp> components;
    private final Map<Class<? extends AbstractElementCmp>, Integer> componentsOrder;
    //Used to build the asset
    @Getter private Class<T> type;
    //The logger for error logging
    protected final Logger logger;
    @Setter
    @Getter
    private boolean child;

    @Setter
    private IContainer parent;

    public AbstractElement(Class<T> type) {
        this.type = type;
        this.logger = LogManager.getLogger(getClass());
        this.components = Maps.newHashMap();
        this.componentsOrder = Maps.newHashMap();
    }

    /**
     * @return returns the built stack, with the entity
     */
    public T build() throws GuiException {
        if (!CoreContext.has(World.class))
            throw new GuiException("Error when building \"" + getClass().getSimpleName() + "\" element, no ecs world found in core context!");
        //Grab the world instance
        var world = CoreContext.get(World.class);
        //We want to dispose of the entity when building if it's not a null entity
        if (entity == EntityRef.NULL) {
            entity = world.createEntity();
        }

        entity.removeAll();
        preBuild();
        //Add all of the components
        components.forEach((type, component) -> {
            entity.add(component);
            logger.debug("Added component {} to element {}, with entity id of {}", component.name(), name(), entity.getId());
        });
        logger.info("Successfully built element {}, with entity id of {}", name(), entity.getId());
        return cast();
    }

    /**
     * Adds a component to the component list
     * TODO: put the component class with the correct order into the componentsOrder map
     */
    public boolean add(AbstractElementCmp component) {
        if (components.containsKey(component.getClass())) {
            logger.warn("Couldn't add component {} because there's already an instance of it. You may only have 1 component of a given type", component.name());
            return false;
        } else {
            //We store the component
            components.put(component.getClass(), component);
            //Store order here
            componentsOrder.put(component.getClass(), components.size() - 1);
            //and we log the relative data
            logger.debug("Added component {}, to element {} with an index of {}.", component.name(), name(), components.size() - 1);
            return true;
        }
    }

    /**
     * This will add to the entity and the components
     */
    public void addDirect(AbstractElementCmp component) {
        if (add(component) && entity != EntityRef.NULL) {
            entity.add(component);
        }
    }

    /**
     * @return returns the parent or empty if it's null
     */
    @Nullable
    public IContainer getParent() {
        return parent;
    }


    /**
     * @return computes the correct container
     */
    @Nullable
    public IContainer container(boolean root) {
//        //If we aren't a child, and the current type is a type of container, then it's the root element we're trying to get
//        if (!this.isChild() && this instanceof IContainer)
//            return CoreContext.get(Root.class);
        if (this.isChild() && parent != null) {
            if (!parent.isChild() || !root)
                return parent;
            else {
                return parent.container(root);
            }
        }
        return CoreContext.get(Root.class);
    }

    /**
     * @return returns a collection of the components
     */
    public Collection<AbstractElementCmp> components() {
        return components.values();
    }


    /**
     * @return returns the component by the given type, and casts it to the type
     */
    public <U extends AbstractElementCmp> Optional<U> get(Class<U> type) {
        if (!components.containsKey(type)) {
//            logger.warn("No component found in \"{}\" with the type \"{}\"", name(false), StringUtils.removeEndIgnoreCase(type.getSimpleName(), "cmp"));
            return Optional.empty();
        }
        return Optional.of(type.cast(components.get(type)));
    }

    /**
     * @return returns true if the element has the given component
     */
    public <U extends AbstractElementCmp> boolean has(Class<U> type) {
        return components.containsKey(type);
    }

    /**
     * @return returns the logger, that is used for logging inside helper interfaces
     */
    public Logger logger() {
        return logger;
    }

    /**
     * @return returns the entity
     */
    public EntityRef entity() {
        return this.entity;
    }

    /**
     * @return returns the casted type to the abstract element type
     */
    public <R extends IElement<T>> R cast(Class<R> type) {
        return type.cast(this);
    }

    @Override
    public int compareTo(IElement o) {
        if (o.entity() != EntityRef.NULL && this.entity != EntityRef.NULL) {
            if (o.entity().has(BoundsCmp.class) && entity().has(BoundsCmp.class)) {
                var mine = entity.get(BoundsCmp.class);
                var other = o.entity().get(BoundsCmp.class);
                var value = (int) mine.getY() - (int) other.getY();
                return value;
            }
        }
        return 0;
    }
}
