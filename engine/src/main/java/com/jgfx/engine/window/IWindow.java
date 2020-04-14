package com.jgfx.engine.window;


/**
 * Represents some generic window. This allows for us to have different kinds of windows, for example
 * the client will likely always use a GLFW window, while the server will likely be window-less or
 * possible some kind of javafx/swing implementation
 */
public interface IWindow {
    String getTitle();

    float getWidth();

    float getHeight();

    float getFbWidth();

    float getFbHeight();

    /**
     * @return returns computed aspect ratio
     */
    default float getAspect() {
        return getFbWidth() / getFbHeight();
    }

    float getWidthScale();

    float getHeightScale();

    boolean isFullscreen();

    boolean isVsync();

    boolean isResizable();


    boolean isFocused();

    void setFocused(boolean focused);

    void init();

    void process();

    void dispose();

    /**
     * Called when the window has requested close
     *
     * @return returns close request
     */
    boolean isCloseRequested();

}
