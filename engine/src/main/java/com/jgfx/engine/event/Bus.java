package com.jgfx.engine.event;

import com.google.common.eventbus.EventBus;

/**
 * represents the different types of event buses
 */
public enum Bus {
    LOGIC(new EventBus("logic")), NETWORK(new EventBus("networking")), ECS(new EventBus("entities")), ASSET(new EventBus("assets")), GUI(new EventBus("gui"));
    private EventBus bus;

    Bus(EventBus bus) {
        this.bus = bus;
    }

    /**
     * Posts an event to the given bus
     *
     * @param event the event to post
     */
    public void post(Object event) {
        bus.post(event);
    }

    /**
     * Registers an event listener to the given bus
     *
     * @param listener the listener to register
     */
    public void register(Object listener) {
        bus.register(listener);
    }
}
