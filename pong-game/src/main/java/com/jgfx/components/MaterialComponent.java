package com.jgfx.components;

import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.ecs.component.Component;

public class MaterialComponent implements Component {
    public final ResourceUrn shapeUrn;
    public final ResourceUrn textureUrn;

    public MaterialComponent(ResourceUrn shapeUrn, ResourceUrn textureUrn) {
        this.shapeUrn = shapeUrn;
        this.textureUrn = textureUrn;
    }

    public MaterialComponent(String shapeUrn, String textureUrn) {
        this(new ResourceUrn(shapeUrn), new ResourceUrn(textureUrn));
    }
}
