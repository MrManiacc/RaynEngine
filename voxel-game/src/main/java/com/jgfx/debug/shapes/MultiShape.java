package com.jgfx.debug.shapes;

import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.text.DecimalFormat;

/**
 * Represents a shape that multiple children
 */
public class MultiShape extends IShape {
    private final IShape[] children;
    @Getter
    @Setter
    private Vector3f position;
    private final Matrix4f modelMatrix;
    private Vao model;

    public MultiShape(Vector3f position, Vector4f color, IShape... children) {
        super(color);
        this.position = position;
        this.children = children;
        this.modelMatrix = new Matrix4f();
    }

    @Override
    public void build() {
        build(true);
    }

    @Override
    protected void build(boolean multi) {
        for (var child : children) {
            child.meshData = this.meshData;
            child.color = this.color;
            child.build(true);
        }
        buildModel();
        loaded = true;
    }

    /**
     * This will build the model
     */
    private void buildModel() {
        if (model != null)
            model.delete();
        //Creates a shape with
        model = Vao.create(2);
        model.bind();
        model.createAttribute(meshData.getVertices(), 3);
        model.createAttribute(meshData.getUvs(), 2);
        model.createIndexBuffer(meshData.getIndices());
        model.unbind();
    }

    @Override
    public void draw(Shader shader) {
        shader.loadMat4("modelMatrix", modelMatrix());
        model.draw();
    }

    /**
     * @return returns the computed model matrix
     */
    private Matrix4f modelMatrix() {
        return modelMatrix.identity().translate(position).scale(1);
    }
}
