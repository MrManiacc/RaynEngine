package com.jgfx.debug.shapes;


import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.utils.MeshData;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4f;

/**
 * Represents a generic shape
 */
public abstract class IShape implements Component {
    @Getter protected MeshData meshData;
    @Getter protected boolean loaded;
    @Getter protected Vector4f color;
    @Getter
    @Setter
    protected boolean wireframe;

    public IShape(Vector4f color) {
        this.color = color;
        this.meshData = new MeshData();
        this.loaded = false;
        this.wireframe = false;
    }


    /**
     * Creates the mesh
     */
    public void build() {
        build(false);
        loaded = true;
    }

    /**
     * Creates the mesh
     */
    protected abstract void build(boolean multi);

    /**
     * Draws the mesh
     */
    public void draw(Shader shader) {
    }

}
