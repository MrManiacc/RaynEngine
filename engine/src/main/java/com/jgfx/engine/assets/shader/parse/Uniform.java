package com.jgfx.engine.assets.shader.parse;

import lombok.Getter;

/**
 * Represents a uniform, which is used to automatically map data in shader
 */
public class Uniform extends Define implements Comparable<Uniform> {
    @Getter
    private String target;
    @Getter
    private String type;

    public Uniform(int line, String[] source, String element) {
        this.line = line;
        this.source = source;
        this.name = parse(element);
    }

    public Uniform(String name, String type) {
        this.name = name;
        this.type = type;
    }


    public Uniform(int line, Uniform copy) {
        this.line = line;
        this.name = copy.name;
        this.type = copy.type;
        this.target = copy.target;
        this.setInFrag(copy.isInFrag());
        this.setInVertex(copy.isInVertex());
    }

    /**
     * Parses the uniform
     *
     * @param input the input data
     * @return returns the name
     */
    @Override
    protected String parse(String input) {
        type = input.split(":")[1].trim();
        target = input.substring(input.indexOf("(") + 1, input.lastIndexOf(")"));
        if (target.equalsIgnoreCase("vert")) {
            setInVertex(true);
            setInFrag(false);
        } else if (target.equalsIgnoreCase("frag")) {
            setInFrag(true);
            setInVertex(false);
        } else {
            setInVertex(true);
            setInFrag(true);
        }
        return input.substring(input.contains("->") ? input.indexOf("->") + 2 : 0, input.indexOf("(")).trim();
    }

    @Override
    public String toString() {
        return "Uniform{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", line='" + line + '\'' +
                '}';
    }

    /**
     * Serialize's the data
     *
     * @return returns the glsl representation of the uniform
     */
    public String serialize() {
        return "uniform " + type + " " + name + ";";
    }


    @Override
    public int compareTo(Uniform o) {
        var value = this.getPriority() - o.getPriority();
        if (value == 0)
            return this.name.compareTo(o.name);
        return value;
    }

    /**
     * Gets the priority based upon the type
     * Lower is higher priority
     *
     * @return the priority
     */
    private int getPriority() {
        switch (this.type) {
            case "mat4":
                return 0;
            case "mat3":
                return 1;
            case "vec4":
                return 2;
            case "vec3":
                return 3;
            case "vec2":
                return 4;
            case "float":
                return 5;
            case "int":
                return 6;
            case "bool":
                return 7;
            case "sampler2D":
                return 8;
            default:
                return Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Uniform) {
            Uniform other = (Uniform) obj;
            var nameEquals = other.name.equals(this.name);
            var shaderMatch = other.isInFrag() == this.isInFrag() && other.isInVertex() == this.isInVertex();
            return nameEquals && shaderMatch;
        }
        return false;
    }
}
