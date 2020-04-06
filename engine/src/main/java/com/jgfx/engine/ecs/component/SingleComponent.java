package com.jgfx.engine.ecs.component;


import com.jgfx.assets.urn.ResourceUrn;

/**
 * A single entity component marks an entity to be mapped to the given resource Urn
 */
public class SingleComponent implements Component {
    public final ResourceUrn urn;

    public SingleComponent(ResourceUrn urn) {
        this.urn = urn;
    }

    public SingleComponent(String urn) {
        this(new ResourceUrn(urn));
    }

}
