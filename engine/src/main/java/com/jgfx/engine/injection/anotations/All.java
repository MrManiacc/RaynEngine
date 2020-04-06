package com.jgfx.engine.injection.anotations;


import com.jgfx.engine.ecs.component.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Auto-configures fields or systems pertaining to aspects.
 *
 * <p>fields are configured
 * during{@link EntitySystem#initialize()}</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface All {
    /**
     * The resource urn of the asset, which is used to inject the asset value
     *
     * @return required types
     */
    Class<? extends Component>[] value() default {};

}
