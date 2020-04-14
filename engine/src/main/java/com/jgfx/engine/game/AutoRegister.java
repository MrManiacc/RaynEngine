package com.jgfx.engine.game;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoRegister {
    int value() default -1;

    /**
     * @return returns the order in which to register this asset, if -1, then we just append it next
     */
    Class[] after() default {};
}
