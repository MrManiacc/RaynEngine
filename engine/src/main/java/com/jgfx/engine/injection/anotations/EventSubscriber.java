package com.jgfx.engine.injection.anotations;


import com.jgfx.engine.event.Bus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventSubscriber {
    Bus[] value() default Bus.LOGIC;
}
