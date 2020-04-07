package com.jgfx.utils;

import com.google.common.collect.Lists;
import com.jgfx.engine.utils.JMath;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import java.util.List;

/**
 * A helper class that contains mesh data
 */
public class MeshData {
    private final List<Float> vertices = Lists.newArrayList();
    private final List<Float> uvs = Lists.newArrayList();
    private final List<Integer> indices = Lists.newArrayList();
    private final List<Float> normals = Lists.newArrayList();
    private final Vector2f uvBuffer = new Vector2f();

    /**
     * Adds a vertices to the list of vertices
     *
     * @param x the x to add
     * @param y the y to add
     * @param z the z to add
     */
    public void addVertex(float x, float y, float z) {
        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
    }

    /**
     * Adds a normal to the list of vertices
     *
     * @param x the x to add
     * @param y the y to add
     * @param z the z to add
     */
    public void addNormal(float x, float y, float z) {
        normals.add(x);
        normals.add(y);
        normals.add(z);
    }

    /**
     * Adds a uv to the list of uvs
     *
     * @param x the x to add
     * @param y the y to add
     */
    public void addUv(float x, float y) {
        uvs.add(x);
        uvs.add(y);
    }

    /**
     * Adds an index to the indices
     *
     * @param indices index to add
     */
    public void addIndices(int... indices) {
        for (int i = 0; i < indices.length; i++)
            this.indices.add(indices[i]);
    }

    /**
     * Gets the current vertex count
     *
     * @return returns vertex count
     */
    public int getVertexCount() {
        return this.vertices.size() / 3;
    }

    /**
     * Gets the vertices as an array
     *
     * @return returns vert array
     */
    public float[] getVertices() {
        var array = new float[vertices.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = vertices.get(i);
        vertices.clear();
        return array;
    }

    /**
     * Gets the vertices as an array
     *
     * @return returns vert array
     */
    public float[] getUvs() {
        var array = new float[uvs.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = uvs.get(i);
        uvs.clear();
        return array;
    }

    /**
     * Gets the vertices as an array
     *
     * @return returns vert array
     */
    public int[] getIndices() {
        var array = new int[indices.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = indices.get(i);
        indices.clear();
        return array;
    }

    /**
     * Gets the vertices as an array
     *
     * @return returns vert array
     */
    public float[] getNormals() {
        var array = new float[normals.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = normals.get(i);
        normals.clear();
        return array;
    }
}
