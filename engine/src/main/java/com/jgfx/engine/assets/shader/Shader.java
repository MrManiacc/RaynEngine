package com.jgfx.engine.assets.shader;

import com.google.common.collect.Maps;
import com.jgfx.assets.Asset;
import com.jgfx.assets.type.AssetType;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.shader.parse.Bind;
import com.jgfx.engine.assets.shader.parse.Uniform;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.*;

public class Shader extends Asset<ShaderData> {
    private int programID, vertexID, fragmentID;
    @Getter
    private final Map<String, Integer> uniforms = Maps.newHashMap();
    private final FloatBuffer matrixBuffer;
    private static final Logger logger = LogManager.getLogger();

    /**
     * The constructor for an asset. It is suggested that implementing classes provide a constructor taking both the urn, and an initial AssetData to load.
     *
     * @param urn       The urn identifying the asset.
     * @param assetType The asset type this asset belongs to.
     */
    public Shader(ResourceUrn urn, AssetType<?, ShaderData> assetType, ShaderData data) {
        super(urn, assetType);
        matrixBuffer = BufferUtils.createFloatBuffer(16);
        reload(data);
    }

    /**
     * Load the shader asset
     *
     * @param data The data to load.
     */
    public void reload(ShaderData data) {
        if (data.isExecutable()) {
            data.compile();
            loadShader(data);
        }
    }

    /**
     * Loads the actual shaders with the provided data
     *
     * @param data the data to load
     */
    private void loadShader(ShaderData data) {
        vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexID, data.getVertex());
        GL20.glCompileShader(vertexID);
        if (GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(vertexID, 500));
            System.err.println("Failed to compile vertex shader");
            System.exit(-1);
        }

        fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentID, data.getFragment());
        GL20.glCompileShader(fragmentID);
        if (GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(fragmentID, 500));
            System.err.println("Failed to compile fragment shader");
            System.exit(-1);
        }
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexID);
        GL20.glAttachShader(programID, fragmentID);
        //Parse the binds

        data.getBinds().forEach(parseBinds);

        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        //parse the uniforms
        data.getUniforms().forEach(parseUniforms);

        logger.info("Shader {} loaded successfully", this.getUrn().toString());
    }

    /**
     * Parses the binds for the shader
     */
    private Consumer<Bind> parseBinds = bind ->
    {
        GL20.glBindAttribLocation(programID, bind.getAttribute(), bind.getName());
    };

    /**
     * Parses the uniforms for the shader
     */
    private Consumer<Uniform> parseUniforms = uniform ->
    {
        this.uniforms.put(uniform.getName(), GL20.glGetUniformLocation(programID, uniform.getName()));
    };


    /**
     * Pass a uniform float to shader
     *
     * @param name  uniform's name
     * @param value the value of the uniform
     */
    public void loadFloat(String name, float value) {
        GL20.glUniform1f(uniforms.get(name), value);
    }


    /**
     * Pass a vec3 to a shader
     *
     * @param name the uniforms name
     * @param vec  the vec to passed to the shader
     */
    public void loadVec3(String name, Vector3f vec) {
        GL20.glUniform3f(uniforms.get(name), vec.x, vec.y, vec.z);
    }


    /**
     * loads vec4 to the shader
     *
     * @param name the vec4 name in the shader
     * @param vec  the vec4 value
     */
    public void loadVec4(String name, Vector4f vec) {
        GL20.glUniform4f(uniforms.get(name), vec.x, vec.y, vec.z, vec.w);
    }

    /**
     * loads boolean to the shader
     *
     * @param name  the boolean name in the shader
     * @param value the boolean value
     */
    public void loadBool(String name, boolean value) {
        int val = (value) ? 1 : 0;
        glUniform1i(uniforms.get(name), val);
    }

    /**
     * loads matrix to the shader
     *
     * @param name the matrix name in the shader
     * @param mat  the matrix value
     */
    public void loadMat4(String name, Matrix4f mat) {
        mat.get(matrixBuffer);
        glUniformMatrix4fv(uniforms.get(name), false, matrixBuffer);
    }

    /**
     * Loads an int
     */
    public void loadInt(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }


    /**
     * loads matrix to the shader
     *
     * @param name the matrix name in the shader
     */
    public void loadVec2(String name, Vector2f vec) {
        GL20.glUniform2f(uniforms.get(name), vec.x, vec.y);
    }

    /**
     * loads matrix to the shader
     *
     * @param name the matrix name in the shader
     */
    public void loadVec2i(String name, Vector2i vec) {
        GL20.glUniform2i(uniforms.get(name), vec.x, vec.y);
    }

    /**
     * Dispose of the shader
     */
    public void dispose() {
        GL20.glDetachShader(programID, vertexID);
        GL20.glDetachShader(programID, fragmentID);
        GL20.glDeleteShader(vertexID);
        GL20.glDeleteShader(fragmentID);
        GL20.glDeleteProgram(programID);
    }


    /**
     * Must be called before using shader
     */
    public void start() {
        GL20.glUseProgram(programID);
    }

    /**
     * Must be called after using shader
     */
    public void stop() {
        GL20.glUseProgram(0);
    }

}
