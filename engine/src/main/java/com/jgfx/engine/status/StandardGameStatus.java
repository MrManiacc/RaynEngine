package com.jgfx.engine.status;

import lombok.Getter;

public enum StandardGameStatus implements EngineStatus {
    PREPARING_SUBSYSTEMS("Preparing Subsystems..."),
    INITIALIZING_ASSET_MANAGEMENT("Initializing Asset Management..."),
    INJECTING_INSTANCES("Injecting instances to Subsystesm..."),
    INITIALIZING_ENTITY_SYSTEMS("Initializing entity systems..."),
    INITIALIZING_MODULE_MANAGER("Initializing Module Management..."),
    INITIALIZING_LOWLEVEL_OBJECT_MANIPULATION("Initializing low-level object manipulators..."),
    INITIALIZING_ASSET_TYPES("Initializing asset types..."),
    UNSTARTED("Unstarted"),
    LOADING("Loading"),
    RUNNING("Running"),
    SHUTTING_DOWN("Shutting down..."),
    DISPOSED("Shut down");

    @Getter
    private boolean processing = false;
    @Getter
    private float progress = 0;
    @Getter
    private final String description;

    StandardGameStatus(String description) {
        this.description = description;
    }
}
