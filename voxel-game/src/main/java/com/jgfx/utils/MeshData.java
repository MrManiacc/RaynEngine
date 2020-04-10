package com.jgfx.utils;

import com.google.common.collect.Lists;
import com.jgfx.engine.utils.JMath;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A helper class that contains mesh data
 */
public class MeshData {
    private final List<Float> vertices = Lists.newArrayList();
    private final List<Float> colors = Lists.newArrayList();
    private final List<Float> uvs = Lists.newArrayList();
    private final List<Integer> indices = Lists.newArrayList();
    private final List<Float> normals = Lists.newArrayList();
    private final Vector2f uvBuffer = new Vector2f();

    /**
     * Clears the mesh data
     */
    public void clear() {
        vertices.clear();
        uvs.clear();
        indices.clear();
        normals.clear();
    }


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
     * Adds a vertices to the list of vertices
     *
     * @param x the x to add
     * @param y the y to add
     * @param z the z to add
     */
    public void addVertices(FloatBuffer vertices) {
        var verts = new float[vertices.capacity()];
        vertices.get(verts);
        for (var i = 0; i < verts.length; i++) {
            this.vertices.add(verts[i]);
        }
    }

    /**
     * Adds a vertices to the list of vertices
     *
     * @param x the x to add
     * @param y the y to add
     * @param z the z to add
     */
    public void addIndices(IntBuffer indices) {
        int[] intArray = new int[indices.capacity()];
        indices.get(intArray);
        List<Integer> list = Arrays.stream(intArray).boxed().collect(Collectors.toList());
        this.indices.addAll(list);
    }

    /**
     * Adds a vertices to the list of vertices
     *
     * @param x the x to add
     * @param y the y to add
     * @param z the z to add
     */
    public void addColor(float r, float g, float b, float a) {
        colors.add(r);
        colors.add(g);
        colors.add(b);
        colors.add(a);
    }

    /**
     * Adds a vertices to the list of vertices
     *
     * @param x the x to add
     * @param y the y to add
     * @param z the z to add
     */
    public void addVertex(float x, float y) {
        vertices.add(x);
        vertices.add(y);
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
        return ArrayUtils.toPrimitive(vertices.toArray(new Float[0]), 0.0F);
    }

    /**
     * Gets the vertices as an array
     *
     * @return returns vert array
     */
    public float[] getUvs() {
        return ArrayUtils.toPrimitive(uvs.toArray(new Float[0]), 0.0F);
    }

    /**
     * Gets the vertices as an array
     *
     * @return returns vert array
     */
    public int[] getIndices() {
        return ArrayUtils.toPrimitive(indices.toArray(new Integer[0]), 0);
    }

    /**
     * Gets the vertices as an array
     *
     * @return returns vert array
     */
    public float[] getNormals() {
        return ArrayUtils.toPrimitive(normals.toArray(new Float[0]), 0.0F);
    }

    /**
     * Gets the colors as an array
     *
     * @return returns the color array
     */
    public float[] getColors() {
        return ArrayUtils.toPrimitive(colors.toArray(new Float[0]), 0.0F);
    }

    public void addUvs(FloatBuffer uvs) {
        var uv = new float[uvs.capacity()];
        uvs.get(uv);
        for (var i = 0; i < uv.length; i++) {
            this.vertices.add(uv[i]);
        }
    }
}
