package com.jgfx.engine.glfw;

import com.google.common.util.concurrent.AtomicDouble;
import com.jgfx.engine.event.Bus;
import com.jgfx.engine.window.IWindow;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Represents a generic glfw window
 */
public class GlfwWindow implements IWindow {
    @Getter
    private String title;
    private AtomicDouble width, height;

    private AtomicDouble fbWidth, fbHeight;
    @Getter
    private boolean fullscreen;
    @Getter
    private boolean vsync;
    @Getter
    private boolean resizable;
    @Getter
    private long handle;
    @Getter
    private float widthScale, heightScale;
    private static final Logger logger = LogManager.getLogger(IWindow.class);

    public GlfwWindow(String title, int width, int height, boolean fullscreen, boolean vsync, boolean resizable) {
        this.title = title;
        this.width = new AtomicDouble(width);
        this.height = new AtomicDouble(height);
        this.fullscreen = fullscreen;
        this.vsync = vsync;
        this.resizable = resizable;
        this.fbWidth = new AtomicDouble(width);
        this.fbHeight = new AtomicDouble(height);
    }

    /**
     * @return returns true if the game window is focused
     */
    @Override
    public boolean isFocused() {
        return glfwGetInputMode(handle, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
    }

    /**
     * Sets the focused state of the window
     */
    @Override
    public void setFocused(boolean focused) {
        glfwSetInputMode(handle, GLFW_CURSOR, focused ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    /**
     * Initialize the window
     */
    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        setHints();
        setupWindow();
        finishWindow();
    }

    /**
     * Sets the hints
     */
    private void setHints() {
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        handle = glfwCreateWindow((int) width.get(), (int) height.get(), title, 0, 0);
        if (handle == 0)
            throw new RuntimeException("Failed to create the GLFW window");
        glfwSetWindowContentScaleCallback(handle, (handle, xscale, yscale) -> {
            widthScale = xscale;
            heightScale = yscale;
        });
    }

    /**
     * Centers the window
     */
    private void setupWindow() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*
            IntBuffer pFbWidth = stack.mallocInt(1); // int*
            IntBuffer pFbHeight = stack.mallocInt(1); // int*
            FloatBuffer sx = stack.mallocFloat(1);
            FloatBuffer sy = stack.mallocFloat(1);
            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(handle, pWidth, pHeight);

            center();
//            glfwSetWindowPos(handle, 3800, 100);

            glfwGetWindowContentScale(handle, sx, sy);
            widthScale = sx.get(0);
            heightScale = sy.get(0);
            glfwGetFramebufferSize(handle, pFbWidth, pFbHeight);
            this.fbWidth.set(pFbWidth.get(0));
            this.fbHeight.set(pFbHeight.get(0));
            glfwSetWindowSizeCallback(handle, new GLFWWindowSizeCallback() {
                @Override
                public void invoke(long window, int w, int h) {
                    logger.info("Window resized from [{}, {}] to [{}, {}]", width.get(), height.get(), w, h);
                    width.set(w);
                    height.set(h);
                    glfwGetFramebufferSize(handle, pFbWidth, pFbHeight);
                    fbWidth.set(pFbWidth.get(0));
                    fbHeight.set(pFbHeight.get(0));
                    Bus.LOGIC.post(new WindowResizeEvent());

                    glfwGetWindowContentScale(handle, sx, sy);
                    widthScale = sx.get(0);
                    heightScale = sy.get(0);
                    center();

                }
            });

        }
    }

    public void center() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(handle, pWidth, pHeight);
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            // Center the window

            glfwSetWindowPos(handle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }
    }

    public float getFbWidth() {
        return (float) fbWidth.get();
    }

    public float getFbHeight() {
        return (float) fbHeight.get();
    }

    public float getWidth() {
        return (float) width.get();
    }

    public float getHeight() {
        return (float) height.get();
    }

    /**
     * Sets the window size, which is used for cfg files
     */
    public void setSize(int width, int height) {
        glfwSetWindowSize(handle, width, height);
    }

    /**
     * Sets the window title, used for cfgs
     */
    public void setTitle(String title) {
        glfwSetWindowTitle(handle, title);
        this.title = title;
    }

    /**
     * Sets the window to vsync
     */
    public void setVsync(boolean vsync) {
        glfwSwapInterval(vsync ? 1 : 0);
        this.vsync = vsync;
    }

    /**
     * Makes the window resizable
     */
    public void setResizable(boolean resizable) {
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE); // the window will be resizable
        this.resizable = resizable;
    }

    /**
     * Registers a key callback, which should be used by the input system
     *
     * @param callback the input callback
     */
    public void registerKeyCallback(GLFWKeyCallback callback) {
        glfwSetKeyCallback(handle, callback);
    }

    /**
     * Registers a mouse position callback
     *
     * @param callback the mouse position callback
     */
    public void registerMousePositionCallback(GLFWCursorPosCallback callback) {
        glfwSetCursorPosCallback(handle, callback);
    }

    /**
     * Registers a mouse button callback
     *
     * @param callback the mouse button callback
     */
    public void registerMouseButtonCallback(GLFWMouseButtonCallback callback) {
        glfwSetMouseButtonCallback(handle, callback);
    }

    /**
     * Registers a mouse scroll callback
     *
     * @param callback the mouse scroll callback
     */
    public void registerMouseScrollCallback(GLFWScrollCallback callback) {
        glfwSetScrollCallback(handle, callback);
    }

    /**
     * Registers a char callback
     *
     * @param callback the char callback
     */
    public void registerCharCallback(GLFWCharCallback callback) {
        glfwSetCharCallback(handle, callback);
    }

    /**
     * Finishes the window
     */
    private void finishWindow() {
        // Make the OpenGL context current
        glfwMakeContextCurrent(handle);
        // Enable v-sync
        glfwSwapInterval(vsync ? 1 : 0);

        // Make the window visible
        glfwShowWindow(handle);

        GL.createCapabilities();

    }

    /**
     * Checks to see if the glfw window should close or not
     *
     * @return returns true if close has been requested
     */
    @Override
    public boolean isCloseRequested() {
        return glfwWindowShouldClose(handle);
    }

    /**
     * Poll the window
     */
    @Override
    public void process() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }


    /**
     * Dispose of the window
     */
    public void dispose() {
        glfwSetWindowShouldClose(handle, true);

        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
