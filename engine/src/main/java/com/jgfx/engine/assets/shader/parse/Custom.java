package com.jgfx.engine.assets.shader.parse;

import lombok.Getter;

/**
 * Represents a custom piece of code that can be imported
 */
public class Custom extends Define implements Comparable<Custom> {
    @Getter
    private String value;

    public Custom(int line, String[] source) {
        super(line, source);
    }

    public Custom(int line, Custom copy) {
        this.line = line;
        this.name = copy.name;
        this.value = copy.value;
    }

    /**
     * Parses the custom type
     *
     * @param input input line
     * @return returns name
     */
    @Override
    protected String parse(String input) {
        this.value = input.substring(input.indexOf("\"") + 1, input.lastIndexOf("\""));
        return input.substring(input.indexOf(" ") + 1, input.indexOf("->")).trim();
    }

    /**
     * Customs are just text so we return the text value
     *
     * @return returns the text value
     */
    @Override
    public String serialize() {
        return value;
    }

    @Override
    public String toString() {
        return "Custom{" +
                "value='" + value + '\'' +
                ", line=" + line +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(Custom o) {
        return this.name.compareTo(o.name);
    }

}
