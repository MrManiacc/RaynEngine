package com.jgfx.engine.assets.model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class represents a stored portion of vertices in opengl memory
 */
public class Vao {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_INT = 4;
    public final int id;
    private List<Vbo> dataVbos = new ArrayList<>();
    private Vbo indexVbo;
    private int indexCount = -1;
    private int attributes;
    private int attribPtr = 0;
    private int drawType;
    //********** DRAW TYPE ***********
    public static final int ELEMENT_TRIANGLES = 0;
    public static final int ELEMENT_TRIANGLE_STRIPS = 1;
    public static final int ELEMENT_TRIANGLE_FAN = 2;
    public static final int ARRAYS_TRIANGLE_STRIPS = 3;
    public static final int ARRAYS_TRIANGLES = 4;
    public static final int ARRAYS_TRIANGLE_FAN = 5;

    private Vao(int id, int attributes, int drawType) {
        this.id = id;
        this.attributes = attributes;
        this.drawType = drawType;
    }

    /**
     * this creates a new opengl vao instance
     *
     * @param attributes the number of attributes to bind
     * @return new vao instance
     */
    public static Vao create(int attributes, int drawType) {
        int id = GL30.glGenVertexArrays();
        return new Vao(id, attributes, drawType);
    }

    /**
     * this creates a new opengl vao instance
     *
     * @param attributes the number of attributes to bind
     * @return new vao instance
     */
    public static Vao create(int attributes) {
        int id = GL30.glGenVertexArrays();
        return new Vao(id, attributes, ELEMENT_TRIANGLES);
    }

    public void setIndexCount(int count) {
        this.indexCount = count;
    }


    public int getIndexCount() {
        return indexCount;
    }

    /**
     * Binds a vao and the specified vbo attributes
     */
    public void bind() {
        GL30.glBindVertexArray(id);
        for (int i = 0; i < attributes; i++) {
            GL20.glEnableVertexAttribArray(i);
        }
    }

    /**
     * Draw's the vao directly
     */
    public void draw() {
        bind();
        switch (drawType) {
            case ELEMENT_TRIANGLES:
                glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
                break;
            case ELEMENT_TRIANGLE_STRIPS:
                glDrawElements(GL_TRIANGLE_STRIP, indexCount, GL_UNSIGNED_INT, 0);
                break;
            case ELEMENT_TRIANGLE_FAN:
                glDrawElements(GL_TRIANGLE_FAN, indexCount, GL_UNSIGNED_INT, 0);
                break;
            case ARRAYS_TRIANGLES:
                glDrawArrays(GL_TRIANGLES, 0, indexCount);
                break;
            case ARRAYS_TRIANGLE_STRIPS:
                glDrawArrays(GL_TRIANGLE_STRIP, 0, indexCount);
                break;
            case ARRAYS_TRIANGLE_FAN:
                glDrawArrays(GL_TRIANGLE_FAN, 0, indexCount);
                break;
        }
        unbind();
    }

    /**
     * Unbinds a vao and it's corresponding vbo id's
     */
    public void unbind() {
        for (int i = 0; i < attributes; i++) {
            GL20.glDisableVertexAttribArray(i);
        }
        GL30.glBindVertexArray(0);
    }

    /**
     * Stores the indices for the vao into a opengl buffer
     *
     * @param indices the indices for the model
     */
    public void createIndexBuffer(int[] indices) {
        this.indexVbo = Vbo.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
        indexVbo.bind();
        indexVbo.storeData(indices);
        this.indexCount = indices.length;
    }

    /**
     * Stores the indices for the vao into a opengl buffer
     */
    public void createIndexBuffer(IntBuffer data) {
        this.indexVbo = Vbo.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
        indexVbo.bind();
        indexVbo.storeData(data);
    }

    /**
     * Creates a vbo to store some specific data into. This is used for rendering
     *
     * @param data     the data to be put into the vbo
     * @param attrSize the size 3 for 3d and 2 for 2d
     */
    public void createAttribute(float[] data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        if (attribPtr == 0 && drawType > ELEMENT_TRIANGLE_FAN)
            this.indexCount = data.length / attrSize;
        dataVbo.bind();
        dataVbo.storeData(data);
        GL20.glVertexAttribPointer(attribPtr++, attrSize, GL11.GL_FLOAT, false, 0, 0);
        dataVbo.unbind();
        dataVbos.add(dataVbo);
    }

    /**
     * Creates a vbo to store some specific data into. This is used for rendering
     *
     * @param data     the data to be put into the vbo
     * @param attrSize the size 3 for 3d and 2 for 2d
     */
    public void createAttribute(int attribute, float[] data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        if (attribute == 0 && drawType > ELEMENT_TRIANGLE_FAN)
            this.indexCount = data.length / attrSize;

        dataVbo.bind();
        dataVbo.storeData(data);
        GL20.glVertexAttribPointer(attribute, attrSize, GL11.GL_FLOAT, false, 0, 0);
        dataVbo.unbind();
        if (dataVbos.get(attribute) != null) {
            dataVbos.remove(attribute);
        } else
            dataVbos.add(dataVbo);
    }

    /**
     * Creates a vbo to store some specific data into. This is used for rendering
     *
     * @param data     the data to be put into the vbo
     * @param attrSize the size 3 for 3d and 2 for 2d
     */
    public void createAttribute(FloatBuffer data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        if (attribPtr == 0 && drawType > ELEMENT_TRIANGLE_FAN)
            this.indexCount = data.limit() / attrSize;
        dataVbo.bind();
        dataVbo.storeData(data);
        GL20.glVertexAttribPointer(attribPtr++, attrSize, GL11.GL_FLOAT, false, 0, 0);
        dataVbo.unbind();
        dataVbos.add(dataVbo);
    }

    /**
     * Creates a vbo to store some specific data into. This is used for rendering
     *
     * @param data     the data to be put into the vbo
     * @param attrSize the size 3 for 3d and 2 for 2d
     */
    public void createIntAttribute(int[] data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL30.glVertexAttribIPointer(attribPtr++, attrSize, GL11.GL_INT, 0, 0);
        dataVbo.unbind();
        dataVbos.add(dataVbo);
    }

    /**
     * Creates a vbo to store some specific data into. This is used for rendering
     *
     * @param data     the data to be put into the vbo
     * @param attrSize the size 3 for 3d and 2 for 2d
     */
    public void createIntAttribute(IntBuffer data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL30.glVertexAttribIPointer(attribPtr++, attrSize, GL11.GL_INT, 0, 0);
        dataVbo.unbind();
        dataVbos.add(dataVbo);
    }

    /**
     * Unloads the vao from memory
     */
    public void delete() {
        GL30.glDeleteVertexArrays(id);
        for (Vbo vbo : dataVbos) {
            vbo.delete();
        }
        indexVbo.delete();
    }

}
