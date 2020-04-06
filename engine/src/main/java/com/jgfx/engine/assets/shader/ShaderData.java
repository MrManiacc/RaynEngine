package com.jgfx.engine.assets.shader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jgfx.assets.data.AssetData;
import com.jgfx.engine.assets.shader.parse.Bind;
import com.jgfx.engine.assets.shader.parse.Custom;
import com.jgfx.engine.assets.shader.parse.Import;
import com.jgfx.engine.assets.shader.parse.Uniform;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ShaderData implements AssetData {
    @Getter
    private final String[] vertexSource, fragmentSource, customLines;
    @Getter
    private Map<String, Uniform> uniformMap = Maps.newHashMap();
    @Getter
    private Map<String, List<Import>> importsMap = Maps.newHashMap();
    @Getter
    private Map<String, Custom> customsMap = Maps.newHashMap();
    @Getter
    private Map<String, Bind> bindsMap = Maps.newHashMap();

    @Getter
    private List<Bind> binds = Lists.newArrayList();
    @Getter
    private List<Uniform> uniforms = Lists.newArrayList();
    @Getter
    private List<Custom> customs = Lists.newArrayList();

    @Getter
    private final String name;
    //This defines if this is a util class or a runnable shader class
    @Getter
    private boolean executable = false;
    @Getter
    private String vertex, fragment;

    public ShaderData(String name, String[] vertexSource, String[] fragmentSource, String[] customLines) {
        this.name = name;
        this.vertexSource = vertexSource;
        this.fragmentSource = fragmentSource;
        this.customLines = customLines;
        determineIfExecutable();
    }

    /**
     * Determines if the shader is executable or not
     */
    private void determineIfExecutable() {
        for (var src : vertexSource) {
            if (src.trim().startsWith("void main"))
                executable = true;
        }
        if (executable) {
            executable = false;
            for (var src : fragmentSource) {
                if (src.trim().startsWith("void main"))
                    executable = true;
            }
        }
    }

    /**
     * Adds an import
     *
     * @param imp the import to add
     */
    public Import addImport(Import imp) {
        if (importsMap.containsKey(imp.getName()))
            importsMap.get(imp.getName()).add(imp);
        else
            importsMap.put(imp.getName(), new ArrayList<>(Collections.singletonList(imp)));
        return imp;
    }

    /**
     * Calculates the line which the main void ends
     */
    private int findLastLine(List<String> input) {
        var mainStarted = false;
        var level = -1;
        for (int i = 0; i < input.size(); i++) {
            var line = input.get(i);
            if (line.trim().startsWith("void main")) {
                mainStarted = true;
            }
            //how many levels deep this line goes
            var inCount = StringUtils.countMatches(line, "{");
            var outCount = StringUtils.countMatches(line, "}");
            if (mainStarted) {
                level += inCount;
                level -= outCount;
                if (level < 0)
                    return i;
            }
        }
        return -1;
    }

    /**
     * adds a new custom type
     *
     * @param custom the custom type
     */
    public Custom addCustom(Custom custom) {
        this.customsMap.put(custom.getName(), custom);
        customs.add(custom);
        return custom;
    }

    /**
     * Compiles the source
     */
    public void compile() {
        sort();
        var compiledVertex = compileShader(vertexSource, true);
        var vertexBuilder = new StringBuilder();
        for (var line : compiledVertex.keySet())
            vertexBuilder.append(compiledVertex.get(line)).append("\n");
        this.vertex = vertexBuilder.toString();
        var compiledFrag = compileShader(fragmentSource, false);
        var fragBuilder = new StringBuilder();
        for (var line : compiledFrag.keySet())
            fragBuilder.append(compiledFrag.get(line)).append("\n");
        this.fragment = fragBuilder.toString();
        //TODO: use these to process other data like passing variables
        //var lastVertLine = findLastLine(vertSrc);
        //var lastFragLine = findLastLine(fragSrc);
    }

    /**
     * Compiles the shader, depending on if it's the vertex or not it will do the correct lookups for variables
     *
     * @param input  the input source
     * @param vertex if true, we're processing the vertex shader, otherwise its the fragment shader
     * @return returns a map of the lines
     */
    private Map<Integer, String> compileShader(String[] input, boolean vertex) {
        //We need to find out if we're inserting it at the given position and if we are we need to
        var output = new HashMap<Integer, String>();
        for (int i = 0; i < input.length; i++) {
            var bind = getBind(i, vertex);
            var uniform = getUniform(i, vertex);
            var custom = getCustom(i, vertex);
            if (custom.isPresent() && !output.containsValue(custom.get().serialize())) {
                var j = i;
                while (output.containsKey(j))
                    j++;
                output.put(j, custom.get().serialize());
            }
            if (bind.isPresent() && !output.containsValue(bind.get().serialize())) {
                var j = i;
                while (output.containsKey(j))
                    j++;
                output.put(j, bind.get().serialize());
            }
            if (uniform.isPresent() && !output.containsValue(uniform.get().serialize())) {
                var j = i;
                while (output.containsKey(j))
                    j++;
                output.put(j, uniform.get().serialize());
            }
        }
        for (var i = 0; i < input.length; i++) {
            var orig = Optional.ofNullable(input[i]);
            if (!isImportLine(i, vertex)) {
                var j = i;
                while (output.containsKey(j))
                    j++;
                if (orig.isPresent())
                    if (!orig.get().isEmpty() && !orig.get().trim().startsWith("//")) {
                        output.put(j, orig.get());
                    }
            }
        }
        return output;
    }

    /**
     * Adds a bind
     *
     * @param bind the bind to add
     */
    public Bind addBind(Bind bind) {
        bindsMap.put(bind.getName(), bind);
        binds.add(bind);
        return bind;
    }


    /**
     * Adds a uniform
     *
     * @param uniform the uniform to add
     */
    public Uniform addUniform(Uniform uniform) {
        uniformMap.put(uniform.getName(), uniform);
        uniforms.add(uniform);
        return uniform;
    }


    /**
     * Get's a bind by the given name
     *
     * @param name the name of the bind
     * @return returns a bind with the given name
     */
    public Optional<Bind> getBind(String name) {
        AtomicReference<Optional<Bind>> bind = new AtomicReference<>(Optional.empty());
        binds.forEach(b -> {
            if (b.getName().equals(name)) bind.set(Optional.of(b));
        });
        return bind.get();
    }

    /**
     * Gets a bind via it's line or empty
     *
     * @param line the line to get
     * @return returns a bind or empty
     */
    public Optional<Bind> getBind(int line, boolean vertex) {
        var output = new AtomicReference<Optional<Bind>>(Optional.empty());
        binds.forEach(bind -> {
            if (bind.getLine() == line && ((vertex && bind.isInVertex()) || (!vertex && bind.isInFrag())))
                output.set(Optional.of(bind));
        });
        return output.get();
    }

    /**
     * Gets a bind via it's line or empty
     *
     * @param line the line to get
     * @return returns a bind or empty
     */
    public Optional<Uniform> getUniform(int line, boolean vertex) {
        var output = new AtomicReference<Optional<Uniform>>(Optional.empty());
        uniforms.forEach(uniform -> {
            if (line == uniform.getLine()) {
                if (uniform.isInFrag() && !vertex) {
                    output.set(Optional.of(uniform));
                }
                if (uniform.isInVertex() && vertex)
                    output.set(Optional.of(uniform));
            }
        });
        return output.get();
    }

    /**
     * Gets a bind via it's line or empty
     *
     * @param line the line to get
     * @return returns a bind or empty
     */
    public Optional<Custom> getCustom(int line, boolean vertex) {
        var output = new AtomicReference<Optional<Custom>>(Optional.empty());
        customs.forEach(custom -> {
            if (custom.getLine() == line) {

                if (custom.isInFrag() && !vertex) {
                    output.set(Optional.of(custom));

                }
                if (custom.isInVertex() && vertex)
                    output.set(Optional.of(custom));
            }
        });
        return output.get();
    }


    /**
     * Get's a bind by the given name
     *
     * @param name the name of the bind
     * @return returns a bind with the given name
     */
    public Optional<Uniform> getUniform(String name) {
        AtomicReference<Optional<Uniform>> uniform = new AtomicReference<>(Optional.empty());
        uniforms.forEach(b -> {
            if (b.getName().equals(name)) uniform.set(Optional.of(b));
        });
        return uniform.get();
    }

    /**
     * Get's a bind by the given name
     *
     * @param name the name of the bind
     * @return returns a bind with the given name
     */
    public Optional<Custom> getCustom(String name) {
        AtomicReference<Optional<Custom>> custom = new AtomicReference<>(Optional.empty());
        customs.forEach(b -> {
            if (b.getName().equals(name)) custom.set(Optional.of(b));
        });
        return custom.get();
    }

    /**
     * Checks to see if the line is an import line
     *
     * @param line   the line to check
     * @param vertex if true, we're checking against the vertex shader, otherwise fragement shader
     * @return returns true if this line was originally an imported line
     */
    private boolean isImportLine(int line, boolean vertex) {
        var output = new AtomicReference<>(false);
        importsMap.values().forEach(imports -> {
            imports.forEach(anImport -> {
                if (anImport.isInVertex() && vertex) {
                    if (anImport.getLine() == line)
                        output.set(true);
                }
                if (anImport.isInFrag() && !vertex)
                    if (anImport.getLine() == line)
                        output.set(true);
            });
        });
        return output.get();
    }

    /**
     * Sorts the values, and updates the lines accordingly
     */
    public void sort() {
        Collections.sort(binds);

        var activeVertLines = new HashSet<Integer>();
        var activeFragLines = new HashSet<Integer>();
        binds.forEach(bind -> {
            if (bind.isInVertex()) {
                while (activeVertLines.contains(bind.getLine())) {
                    bind.setLine(bind.getLine() + 1);
                }
                activeVertLines.add(bind.getLine());
            }
            if (bind.isInFrag()) {
                while (activeFragLines.contains(bind.getLine())) {
                    bind.setLine(bind.getLine() + 1);
                }
                activeFragLines.add(bind.getLine());
            }
        });
        uniforms.forEach(uniform -> {
            if (uniform.isInVertex()) {
                while (activeVertLines.contains(uniform.getLine())) {
                    uniform.setLine(uniform.getLine() + 1);
                }
                activeVertLines.add(uniform.getLine());
            }
            if (uniform.isInFrag()) {
                while (activeFragLines.contains(uniform.getLine())) {
                    uniform.setLine(uniform.getLine() + 1);
                }
                activeFragLines.add(uniform.getLine());
            }
        });
        Collections.sort(uniforms);

    }

    /**
     * Prints the shader data
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(StringUtils.capitalize(name)).append(" Shader").append("{").append("\n");
        if (!customs.isEmpty()) {
            builder.append("\tCustoms{").append("\n");
            customs.forEach(custom -> {
                builder.append("\t\t").append(custom.toString()).append("\n");
            });
            builder.append("\t").append("}").append("\n");
        }
        if (!binds.isEmpty()) {
            builder.append("\tBinds{").append("\n");
            binds.forEach(bind -> {
                builder.append("\t\t").append(bind.toString()).append("\n");
            });
            builder.append("\t").append("}").append("\n");
        }
        if (!uniforms.isEmpty()) {
            builder.append("\tUniforms{").append("\n");
            uniforms.forEach(uniform -> {
                builder.append("\t\t").append(uniform.toString()).append("\n");
            });
            builder.append("\t").append("}").append("\n");
        }

        builder.append("}");
        return builder.toString();
    }
}
