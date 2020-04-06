package com.jgfx.engine.assets.shader.parse;

import lombok.Getter;

/**
 * Represents some bind that can be appended to the shader dynamically
 */
public class Bind extends Define implements Comparable<Bind> {
    @Getter
    private int attribute;
    @Getter
    private String type;
    @Getter
    private boolean passBind;

    public Bind(int line, String[] source) {
        super(line, source);
    }

    public Bind(int line, String[] source, String input) {
        this.line = line;
        this.source = source;
        this.name = parse(input);
    }

    public Bind(int line, String name, int attribute, String type, boolean passBind) {
        this.line = line;
        this.passBind = passBind;
        this.name = name;
        this.attribute = attribute;
        this.type = type;
    }

    /**
     * Creates a copy with the specified line; used for importing
     *
     * @param line the line to set to
     * @param copy the copy
     */
    public Bind(int line, Bind copy) {
        this(line, copy.name, copy.attribute, copy.type, copy.passBind);
    }


    /**
     * Parsed the asset
     *
     * @param input the input line
     * @return returns the name
     */
    @Override
    protected String parse(String input) {
        var name = input.substring(input.contains("->") ? input.indexOf("->") + 2 : 0, input.indexOf("(")).trim();
        this.attribute = Integer.parseInt(input.substring(input.indexOf("(") + 1, input.indexOf(",")));
        this.passBind = Boolean.parseBoolean(input.substring(input.indexOf(",") + 1, input.indexOf(")")).trim());
        this.type = input.substring(input.indexOf(":") + 1).trim();
        return name;
    }

    /**
     * Serialize's the data
     *
     * @return returns the glsl representation of the uniform
     */
    public String serialize() {
        return "in " + type + " " + name + ";";
    }

    @Override
    public String toString() {
        return "Bind{" +
                "name=" + name +
                ", attribute=" + attribute +
                ", type='" + type + '\'' +
                ", passBind=" + passBind +
                ", line=" + line +
                '}';
    }

    @Override
    public int compareTo(Bind o) {
        if (attribute == o.attribute)
            return 0;
        return attribute - o.attribute;
    }
}



