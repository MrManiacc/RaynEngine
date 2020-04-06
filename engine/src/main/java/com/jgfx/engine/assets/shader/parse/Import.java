package com.jgfx.engine.assets.shader.parse;

import com.jgfx.assets.urn.ResourceUrn;
import lombok.Getter;

/**
 * Represents some arbitrary code that will be imported from one file to another
 */
public class Import extends Define {
    @Getter
    private ResourceUrn urn;
    @Getter
    private String definition;


    public Import(int line, String[] source) {
        super(line, source);
    }

    @Override
    protected String parse(String input) {
        var name = input.substring(input.indexOf(" "), input.indexOf("->")).trim();
        urn = new ResourceUrn(name);
        definition = input.substring(input.indexOf("->") + 2).trim();
        return name;
    }

    /**
     * An import isn't supposed to be used like this
     *
     * @return returns the serialized value
     */
    @Override
    public String serialize() {
        return "N/A";
    }

    @Override
    public String toString() {
        return "Import{" +
                "line='" + line + '\'' +
                "definition='" + definition + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
