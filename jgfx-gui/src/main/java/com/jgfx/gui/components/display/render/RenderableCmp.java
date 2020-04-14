package com.jgfx.gui.components.display.render;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.window.IWindow;
import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

/**
 * A component that can be rendered
 */
public class RenderableCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private Vao vao;
    private final Matrix4f modelMatrix;
    @Getter private final Vector4f data;
    @Getter private float angle;

    public RenderableCmp(AbstractElement element) {
        super(element);
        this.modelMatrix = new Matrix4f().identity().translate(0, 0, 0);
        this.data = new Vector4f();
    }

    /**
     * @return this will set the model matrix to identity
     */
    public RenderableCmp begin() {
        modelMatrix.identity();
        return this;
    }

    /**
     * Translates the transform, should be called after {@link RenderableCmp#begin()}
     */
    public RenderableCmp translate(float x, float y) {
        modelMatrix.translate(x, y, 0);
        data.x = x;
        data.y = y;
        return this;
    }

    /**
     * Scales the transform, should be called after {@link RenderableCmp#begin()} or {@link RenderableCmp#translate()}
     */
    public RenderableCmp scale(float x, float y) {
        modelMatrix.scale(x, y, 1);
        data.z = x;
        data.w = y;
        return this;
    }

    /**
     * Scales the transform, should be called after {@link RenderableCmp#begin()} or {@link RenderableCmp#translate()}
     */
    public RenderableCmp rotate(float angle) {
        modelMatrix.rotateZ(angle);
        this.angle = angle;
        return this;
    }


    /**
     * @return returns the resulting transform
     */
    public Matrix4f modelMatrix() {
        return modelMatrix;
    }


    @Override
    public String dataString() {
        return "\"matrix\":" + "\"" + modelMatrix.toString(DecimalFormat.getInstance()).trim().replaceAll("\n", ", ") + "\"";
    }
}
