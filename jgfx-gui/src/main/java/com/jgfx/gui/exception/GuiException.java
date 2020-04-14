package com.jgfx.gui.exception;

/**
 * A generic gui exception
 */
public class GuiException extends Exception{
    public GuiException(String message) {
        super(message);
    }

    public GuiException(String message, Throwable cause) {
        super(message, cause);
    }

    public GuiException(Throwable cause) {
        super(cause);
    }

    public GuiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
