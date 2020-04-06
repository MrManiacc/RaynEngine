package com.jgfx.engine.utils;

import com.google.common.collect.Maps;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.model.Vao;

import java.util.Map;


/**
 * This class will handle the loading and gathering of shapes
 */
public class ShapeUtils {
    private final Map<ResourceUrn, Vao> models = Maps.newHashMap();

    /**
     * Adds a shape to the models list
     *
     * @param urn the shape identifier
     * @param vao the actual renderable shape
     */
    public void add(ResourceUrn urn, Vao vao) {
        models.put(urn, vao);
    }

    /**
     * Adds a shape the model list
     *
     * @param urn the shape identifier
     * @param vao the actual shape
     */
    public void add(String urn, Vao vao) {
        models.put(new ResourceUrn(urn), vao);
    }

    /**
     * Get's a shape via it's urn
     *
     * @param urn the shape's identifier
     * @return returns the shape
     */
    public Vao get(ResourceUrn urn) {
        return models.get(urn);
    }

    /**
     * Get's a shape via it's urn
     *
     * @param urn the shape's identifier
     * @return returns the shape
     */
    public Vao get(String urn) {
        return get(new ResourceUrn(urn));
    }

    /**
     * Checks to see if the given shape is present
     *
     * @param urn the shape's identifier
     * @return returns true if present
     */
    public boolean has(ResourceUrn urn) {
        return models.containsKey(urn);
    }

    /**
     * Checks to see if the given shape is present
     *
     * @param urn the shape's identifier
     * @return returns true if present
     */
    public boolean has(String urn) {
        return has(new ResourceUrn(urn));
    }
}
