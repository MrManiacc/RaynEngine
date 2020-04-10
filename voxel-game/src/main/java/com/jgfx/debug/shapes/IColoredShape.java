package com.jgfx.debug.shapes;

import org.joml.Vector4f;

/**
 * A shape that uses a color
 */
public interface IColoredShape {
    /**
     * @return the red color component
     */
    default float getR() {
        return getColor().x;
    }

    /**
     * @return the green component
     */
    default float getG() {
        return getColor().y;
    }

    /**
     * @return the green component
     */
    default float getB() {
        return getColor().z;
    }

    /**
     * @return the alpha component
     */
    default float getA() {
        return getColor().w;
    }

    /**
     * Sets the color to the input color
     */
    default void setColor(Vector4f color) {
        getColor().set(color);
    }

    /**
     * Sets the color
     */
    default void setColor(float r, float g, float b, float a) {
        getColor().set(r, g, b, a);
    }

    /**
     * @return returns the actual color
     */
    Vector4f getColor();
}
