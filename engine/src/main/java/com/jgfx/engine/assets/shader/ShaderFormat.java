package com.jgfx.engine.assets.shader;

import com.jgfx.assets.files.AbstractAssetFileFormat;
import com.jgfx.assets.files.AssetDataFile;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.shader.parse.Bind;
import com.jgfx.engine.assets.shader.parse.ShaderParser;
import com.jgfx.engine.assets.shader.parse.Uniform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ShaderFormat extends AbstractAssetFileFormat<ShaderData> {
    private static final Logger logger = LogManager.getLogger();

    public ShaderFormat() {
        super("glsl");
    }

    /**
     * Loads the shader data
     *
     * @param urn    The urn identifying the asset being loaded.
     * @param inputs The inputs corresponding to this asset
     * @return returns the shader data
     * @throws IOException
     */
    public ShaderData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        var input = inputs.get(0);
        var stream = input.openStream();
        logger.debug("Loading shader {} from file", urn.toString());
        var lines = Files.readAllLines(input.getPath());
        stream.close();
        var data = buildShader(lines, urn);
        parseUniforms(data.getVertexSource(), data);
        parseUniforms(data.getFragmentSource(), data);
        parseBinds(data.getVertexSource(), data);
        parseBinds(data.getFragmentSource(), data);
        return ShaderParser.parseShader(data, urn);
    }

    /**
     * Parses all of the uniforms
     */
    private void parseUniforms(String[] lines, ShaderData data) {
        for (var l : lines) {
            var line = l.trim();
            if (line.trim().startsWith("uniform")) {
                var elements = line.split(" ");
                var type = elements[1].trim();
                var name = elements[2].replace(";", "").trim();
                data.addUniform(new Uniform(name, type));
            }
        }
    }

    /**
     * Parses all of the binds, attribute index will be in sequential order, starting with 0
     */
    private void parseBinds(String[] lines, ShaderData data) {
        var attribute = 0;
        for (var l : lines) {
            var line = l.trim();
            if (line.trim().startsWith("in")) {
                var elements = line.split(" ");
                var type = elements[1].trim();
                var name = elements[2].replace(";", "").trim();
                data.addBind(new Bind(attribute++, name, type));
            }
        }
    }

    /**
     * Builds the shader data from the shader source inputs
     *
     * @param lines the input to build for
     * @return returns the shader data
     */
    private ShaderData buildShader(List<String> lines, ResourceUrn urn) {
        var customLinesList = new ArrayList<String>();
        var shaderLines = parseShaderLines(lines, customLinesList);
        var vertexSource = new String[shaderLines[1] - shaderLines[0]];
        var fragSource = new String[shaderLines[3] - shaderLines[2]];
        var customLines = new String[customLinesList.size()];
        for (int i = 0; i < customLinesList.size(); i++)
            customLines[i] = customLinesList.get(i);
        for (int i = shaderLines[0], j = 0; i < shaderLines[1]; i++, j++)
            vertexSource[j] = lines.get(i).trim();
        for (int i = shaderLines[2], j = 0; i < shaderLines[3]; i++, j++)
            fragSource[j] = lines.get(i).trim();
        var vertSrc = new ArrayList<String>();
        for (var line : vertexSource) {
            if (!line.trim().startsWith("//"))
                vertSrc.add(line);
        }

        var fragSrc = new ArrayList<String>();
        for (var line : fragSource) {
            if (!line.trim().startsWith("//"))
                fragSrc.add(line);
        }
        return new ShaderData(urn.getFragmentName().toLowerCase(), vertSrc.toArray(new String[0]), fragSrc.toArray(new String[0]), customLines);
    }

    /**
     * Finds the start and stop lines for the given shader,
     *
     * @param lines the shader source
     * @return returns the start and stop of both shaders
     */
    private int[] parseShaderLines(List<String> lines, List<String> customLines) {
        var shaderLines = new int[]{
                -1, -1, -1, -1
        };
        var inVertex = false;
        var inFrag = false;
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i).trim();
            if (line.startsWith("#ifdef")) {
                var id = line.split(" ")[1];
                if (id.equalsIgnoreCase("VERTEX_SHADER")) {
                    shaderLines[0] = i + 1;
                    inVertex = true;
                }
                if (id.equalsIgnoreCase("FRAGMENT_SHADER")) {
                    shaderLines[2] = i + 1;
                    inFrag = true;
                }
            } else if (line.startsWith("#endif")) {
                if (inVertex) {
                    inVertex = false;
                    shaderLines[1] = i;
                }
                if (inFrag) {
                    shaderLines[3] = i;
                    inFrag = false;
                }
            }

            if (!inFrag && !inVertex) {
                customLines.add(line);
            }
        }
        return shaderLines;
    }
}
