package com.jgfx.engine.game;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Game {
    /**
     * @return returns the game's name
     */
    String name();

    /**
     * @return returns the game's version
     */
    String version();
}
