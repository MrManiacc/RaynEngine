package com.jgfx.gui.components.display.size;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;

/**
 * Represents the bounds for a given element which will be computed at runtime
 */
public class BoundsCmp extends AbstractElementCmp {
    @Getter
    private float x, y, width, height, centerX, centerY;

    public BoundsCmp(AbstractElement element) {
        super(element);
    }

    public void set(BoundsCmp other) {
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
        this.centerX = other.centerX;
        this.centerY = other.centerY;
    }

    public void setX(float x) {
        this.x = x;
        this.centerX = x + width;
    }

    public void setY(float y) {
        this.y = y;
        this.centerY = y + height;
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    public void setCenter(float centerX, float centerY) {
        setCenterX(centerX);
        setCenterY(centerY);
    }

    public void setWidth(float width) {
        this.width = width;
        this.centerX = x + width;
    }

    public void setHeight(float height) {
        this.height = height;
        this.centerY = y + height;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
        this.x = centerX - width;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
        this.y = centerY - height;
    }


    @Override
    public String dataString() {
        return "\"x\":" + x + "," +
                "\"y\":" + y + "," +
                "\"width\":" + width + "," +
                "\"height\":" + height + "," +
                "\"centerX\":" + centerX + "," +
                "\"centerY\":" + centerY;
    }

}
