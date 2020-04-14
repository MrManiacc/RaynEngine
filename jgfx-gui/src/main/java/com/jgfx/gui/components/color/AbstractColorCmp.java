package com.jgfx.gui.components.color;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4f;

public abstract class AbstractColorCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private int r = 255, g = 255, b = 255, a = 255; //White by default
    private final Vector4f color = new Vector4f(1, 1, 1, 1);//White by default as well, even though this would be computed

    public AbstractColorCmp(AbstractElement parent) {
        super(parent);
    }

    /**
     * @return returns the correctly computed color
     */
    public Vector4f getColor() {
        return color.set(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }

    @Override
    public String dataString() {
        return "\"red\": " + r + "," +
                "\"green\": " + g + "," +
                "\"blue\": " + b + "," +
                "\"alpha\": " + a;
    }
}
