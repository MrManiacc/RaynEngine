package com.jgfx.debug.shapes;

import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * This class will create a debug outline shape from a given size
 */
public class LineShape extends IShape implements IColoredShape {
    /**
     * this is the model to use
     */
    private Vao model;
    private float length = 5, size = 0.025f;
    private final Vector3f position;
    private final Vector3f rotation;
    private Matrix4f modelMatrix;

    /**
     * Creates an outline shape with min and max
     */
    public LineShape(Vector3f position, float length, float size, Vector4f color) {
        this(position, new Vector3f(), length, size, color);
    }


    /**
     * Creates an outline shape with min and max
     */
    public LineShape(Vector3f position, float length, float size) {
        this(position, length, size, new Vector4f(0, 1, 0, 1));
    }

    /**
     * Creates an outline shape with min and max
     */
    public LineShape(Vector3f position, float length) {
        this(position, length, 0.25f);
    }


    /**
     * Creates an outline shape with min and max
     */
    public LineShape(Vector3f position, Vector3f rotation, float length, float size) {
        this(position, rotation, length, size, new Vector4f(0, 1, 0, 1));
    }

    /**
     * Creates an outline shape with min and max
     */
    public LineShape(Vector3f position, Vector3f rotation, float length) {
        this(position, rotation, length, 0.25f);
    }

    /**
     * Creates an outline shape with min and max
     */
    public LineShape(Vector3f position, Vector3f rotation, float length, float size, Vector4f color) {
        super(color);
        //Default color of red
        this.position = position;
        this.rotation = rotation;
        this.length = length;
        this.size = size;
        this.modelMatrix = new Matrix4f();
    }

    /**
     * This will build the mesh either as a single unit or will add it to the group
     */
    @Override
    protected void build(boolean multi) {
        if (!multi) {
            meshData.clear();
        }

        var height = length / 2.0f;

        var min = new Vector3f(-size, -height, -size);
        min.add(position);
        var max = new Vector3f(size, height, size);
        max.add(position);
        meshData.addVertex(min.x, max.y, max.z);
        meshData.addVertex(min.x, min.y, max.z);
        meshData.addVertex(max.x, max.y, max.z);
        meshData.addVertex(max.x, min.y, max.z);
        addUv(color);
        addIndices();


        meshData.addVertex(max.x, max.y, min.z);
        meshData.addVertex(max.x, min.y, min.z);
        meshData.addVertex(min.x, max.y, min.z);
        meshData.addVertex(min.x, min.y, min.z);
        addUv(color);
        addIndices();

        meshData.addVertex(max.x, max.y, max.z);
        meshData.addVertex(max.x, min.y, max.z);
        meshData.addVertex(max.x, max.y, min.z);
        meshData.addVertex(max.x, min.y, min.z);
        addUv(color);
        addIndices();

        meshData.addVertex(min.x, max.y, min.z);
        meshData.addVertex(min.x, min.y, min.z);
        meshData.addVertex(min.x, max.y, max.z);
        meshData.addVertex(min.x, min.y, max.z);
        addUv(color);
        addIndices();

        if (!multi) {
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
    }

    /**
     * Adds the indices
     */
    private void addIndices() {
        var length = meshData.getVertexCount();
        meshData.addIndices(length - 4);
        meshData.addIndices(length - 3);
        meshData.addIndices(length - 2);

        meshData.addIndices(length - 2);
        meshData.addIndices(length - 3);
        meshData.addIndices(length - 1);
    }

    /**
     * Adds the color to the mesh data
     */
    private void addUv(Vector4f color) {
        meshData.addUv(-1, 1);
        meshData.addUv(-1, -1);
        meshData.addUv(1, 1);
        meshData.addUv(1, -1);
    }

    /**
     * Draws the shape
     */
    @Override
    public void draw(Shader shader) {
        shader.loadMat4("modelMatrix", modelMatrix());
        shader.loadVec4("color", color);
        model.draw();
    }

    /**
     * @return returns the computed model matrix
     */
    public Matrix4f modelMatrix() {
        return modelMatrix.identity().translate(0, 0, 0).rotateX((float) Math.toRadians(rotation.x)).rotateY((float) Math.toRadians(rotation.y)).rotateZ((float) Math.toRadians(rotation.z));
    }
}
