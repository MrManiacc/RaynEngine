package com.jgfx.engine.injection;

/**
 * Called when there is an exception during injection
 */
public class InjectionException extends Exception {
    public InjectionException(String message) {
        super(message);
    }
}
