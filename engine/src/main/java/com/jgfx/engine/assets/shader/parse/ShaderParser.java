package com.jgfx.engine.assets.shader.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jgfx.assets.naming.Name;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.shader.ShaderData;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class will parse the shader and generate the proper source code
 */
public class ShaderParser {
    private static final Map<ResourceUrn, ShaderData> cachedData = Maps.newHashMap();
    private static final Map<ResourceUrn, Custom> globalCustoms = Maps.newHashMap();
    private static final Map<ResourceUrn, Bind> globalBinds = Maps.newHashMap();
    private static final Map<ResourceUrn, Uniform> globalUniforms = Maps.newHashMap();
    private static final Set<ResourceUrn> processedShaders = Sets.newHashSet();

    public ShaderParser() {
    }

    /**
     * This will parse the shader data
     *
     * @param resource the resource urn to parse
     * @return returns an optional shader data
     */
    public static ShaderData parseShader(ShaderData shaderData, ResourceUrn resource) {
        for (int i = 0; i < shaderData.getCustomLines().length; i++)
            processLine(shaderData.getCustomLines(), i, shaderData, false, false);
        for (int i = 0; i < shaderData.getVertexSource().length; i++)
            processLine(shaderData.getVertexSource(), i, shaderData, true, false);
        for (int i = 0; i < shaderData.getFragmentSource().length; i++)
            processLine(shaderData.getFragmentSource(), i, shaderData, false, true);
        if (!isReady(shaderData))
            return null;
        processGlobals(shaderData, resource);
        processImports(shaderData, resource);
        processedShaders.add(resource);
        return shaderData;
    }

    /**
     * Builds the global shader data
     *
     * @param data the data to process
     */
    private static void processGlobals(ShaderData data, ResourceUrn urn) {
        data.getBindsMap().forEach((s, bind) -> {
            var resourceUrn = new ResourceUrn(urn.getModuleName(), new Name(urn.getResourceName() + ":" + urn.getFragmentName()), new Name(s));
            globalBinds.put(resourceUrn, bind);
        });
        data.getUniformMap().forEach((s, imp) -> {
            var resourceUrn = new ResourceUrn(urn.getModuleName(), new Name(urn.getResourceName() + ":" + urn.getFragmentName()), new Name(s));
            globalUniforms.put(resourceUrn, imp);
        });
        data.getCustomsMap().forEach((s, custom) -> {
            var resourceUrn = new ResourceUrn(urn.getModuleName(), new Name(urn.getResourceName() + ":" + urn.getFragmentName()), new Name(s));
            globalCustoms.put(resourceUrn, custom);
        });
    }

    /**
     * Processes the imports for the shader
     *
     * @param data the data to process
     */
    private static void processImports(ShaderData data, ResourceUrn urn) {
        data.getImportsMap().forEach((name, imports) -> {
            imports.forEach(_import -> {
                switch (_import.getDefinition()) {
                    case "BINDS":
                        var binds = getGlobalBinds(new ResourceUrn(name));
                        binds.forEach(bind -> {
                            var b = new Bind(_import.getLine(), bind);
                            b.setInVertex(_import.isInVertex());
                            b.setInFrag(_import.isInFrag());
                            data.addBind(b);
                        });
                        break;
                    case "UNIFORMS":
                        var uniforms = getGlobalUniforms(new ResourceUrn(name));
                        uniforms.forEach(uniform -> {
                            var u = new Uniform(_import.getLine(), uniform);
                            data.addUniform(u);
                        });
                        break;
                    default:
                        var resourceUrn = extractUrn(name, _import.getDefinition());
                        var customs = getGlobalCustoms(resourceUrn);
                        customs.forEach(custom -> {
                            var c = new Custom(_import.getLine(), custom);
                            c.setInVertex(_import.isInVertex());
                            c.setInFrag(_import.isInFrag());
                            data.addCustom(c);
                        });
                        break;
                }
            });
        });
    }

    /**
     * Computes the proper urn from the given input
     *
     * @param input the input urn
     * @return returns the resource urn
     */
    private static ResourceUrn extractUrn(String input, String fragment) {
        if (input.contains(":")) {
            var ids = input.split(":");
            var moduleBuilder = new StringBuilder();
            var name = "";
            for (var i = 0; i < ids.length; i++) {
                if (i < ids.length - 1)
                    moduleBuilder.append(ids[i]).append(":");
                else
                    name = ids[i];
            }
            var module = moduleBuilder.toString();
            module = module.substring(0, module.lastIndexOf(":"));

            return new ResourceUrn(module, name, fragment);
        } else
            return new ResourceUrn(input, fragment);
    }

    /**
     * Get's the binds by the given urn type, will try to get the instance first, then by type
     *
     * @param urn the type to check
     * @return returns a list of binds by the given urn
     */
    private static List<Bind> getGlobalBinds(ResourceUrn urn) {
        List<Bind> output = Lists.newArrayList();
        globalBinds.forEach((resourceUrn, bind) -> {
            if (resourceUrn.equals(urn) || resourceUrn.isOfType(urn)) {
                output.add(bind);
            }
        });
        Collections.sort(output);
        return output;
    }

    /**
     * Get's the binds by the given urn type, will try to get the instance first, then by type
     *
     * @param urn the type to check
     * @return returns a list of binds by the given urn
     */
    private static List<Uniform> getGlobalUniforms(ResourceUrn urn) {
        List<Uniform> output = Lists.newArrayList();
        globalUniforms.forEach((resourceUrn, bind) -> {
            if (resourceUrn.equals(urn))
                output.add(bind);
            else if (resourceUrn.isOfType(urn))
                output.add(bind);
        });
        Collections.sort(output);
        return output;
    }

    /**
     * Gets a global custom via an import
     *
     * @param urn the urn to get the custom by
     * @return returns a list of sorted customs
     */
    private static List<Custom> getGlobalCustoms(ResourceUrn urn) {
        List<Custom> output = Lists.newArrayList();
        globalCustoms.forEach((resourceUrn, custom) -> {
            if (urn.equals(resourceUrn)) {
                output.add(custom);
            }
        });
        Collections.sort(output);
        return output;
    }


    /**
     * this method checks to see if the global imports are present for the given shader
     *
     * @param data the data to process
     * @return returns true if ready
     */
    private static boolean isReady(ShaderData data) {
        var present = new AtomicBoolean(true);
        data.getImportsMap().forEach((urn, imports) -> {
            imports.forEach(anImport -> {
                var _import = new ResourceUrn(anImport.name);
                switch (anImport.getDefinition()) {
                    case "BINDS":
                        var containsBind = new AtomicBoolean(false);
                        globalBinds.forEach((resourceUrn, bind) -> {
                            if (resourceUrn.isOfType(_import))
                                containsBind.set(true);

                        });
                        if (!containsBind.get())
                            present.set(false);
                        break;
                    case "UNIFORMS":
                        var containsUniform = new AtomicBoolean(false);
                        globalUniforms.forEach((resourceUrn, bind) -> {
                            if (resourceUrn.isOfType(_import))
                                containsUniform.set(true);
                        });
                        if (!containsUniform.get())
                            present.set(false);
                        break;
                    default:
                        var custom = new ResourceUrn(_import, anImport.getDefinition());
                        if (!globalCustoms.containsKey(custom))
                            present.set(false);
                        break;
                }
            });
        });


        return present.get();
    }


    /**
     * Process a given line
     */
    private static void processLine(String[] input, int index, ShaderData data, boolean vertex, boolean frag) {
        var line = input[index];
        if (line.trim().startsWith("#define")) {
            var rawLine = line.replaceFirst("#define", "").trim();
            var type = rawLine.split("->")[0].replace("->", "").trim();
            switch (type) {
                case "UNIFORMS":
                    parseUniforms(input, index, data, vertex, frag);
                    break;
                case "BINDS":
                    parseBinds(input, index, data, vertex, frag);
                    break;
                default:
                    if (type.equals(type.toLowerCase()) && type.contains(":") && !type.contains("\"")) {
                        var imported = new Import(index, input);
                        imported.setInVertex(vertex);
                        imported.setInFrag(frag);
                        data.addImport(imported);
                    } else if (line.contains("\"")) {
                        var custom = data.addCustom(new Custom(index, input));
                        custom.setInFrag(frag);
                        custom.setInVertex(vertex);
                    }
                    break;
            }
        }
    }

    /**
     * Parses a collection of uniforms from the uniform line
     */
    private static void parseUniforms(String[] lines, int index, ShaderData shaderData, boolean vert, boolean frag) {
        var line = lines[index];
        if (line.contains(",")) {
            var elements = line.split(",");
            for (var element : elements) {
                var uniform = new Uniform(index, lines, element);
                shaderData.addUniform(uniform);
            }
        } else {
            var uniform = new Uniform(index, lines, line);
            shaderData.addUniform(uniform);
        }
    }

    /**
     * Parses a collection of binds from the binds line
     */
    private static void parseBinds(String[] lines, int index, ShaderData data, boolean vert, boolean frag) {
        var line = lines[index];
        if (line.contains(",")) {
            var start = 0;
            List<String> elements = new ArrayList<>();
            var inParenthesis = false;
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == '(')
                    inParenthesis = true;
                if (line.charAt(i) == ')')
                    inParenthesis = false;
                if (!inParenthesis && line.charAt(i) == ',') {
                    elements.add(line.substring(start, i).trim());
                    start = i;
                }
            }
            if (start != 0)
                elements.add(line.substring(start + 1).trim());
            for (var element : elements) {
                var bind = new Bind(index, lines, element);
                bind.setInFrag(frag);
                bind.setInVertex(vert);
                data.addBind(bind);
            }

        } else {
            var bind = new Bind(index, lines, line);
            bind.setInFrag(frag);
            bind.setInVertex(vert);
            data.addBind(bind);
        }
    }

}
