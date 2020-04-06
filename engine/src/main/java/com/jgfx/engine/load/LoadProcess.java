package com.jgfx.engine.load;

/**
 * A simple process that will be executed in a queue. This will be used mainly inside of the StateLoading
 */
public interface LoadProcess {
    /**
     * @return A message describing the state of the process
     */
    String getMessage();

    /**
     * Runs a single step.
     *
     * @return Whether the overall process is finished
     */
    boolean step();

    /**
     * Begins the loading
     */
    void begin();

}
