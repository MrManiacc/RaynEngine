package com.jgfx.engine.time;

/**
 * A generic timer class
 */
public class GenericTime extends TimeBase {

    public GenericTime() {
        super(System.currentTimeMillis());
    }

    /**
     * Simple current time mills
     *
     * @return returns current time
     */
    protected long getRawTimeInMs() {
        return System.currentTimeMillis();
    }
}
