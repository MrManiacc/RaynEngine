package com.jgfx.engine.status;

/**
 * Called when the engine status is updated
 */
public class EngineStatusUpdatedEvent {
    public final EngineStatus newStatus;
    public final EngineStatus lastStatus;

    public EngineStatusUpdatedEvent(EngineStatus newStatus, EngineStatus lastStatus) {
        this.newStatus = newStatus;
        this.lastStatus = lastStatus;
    }
}
