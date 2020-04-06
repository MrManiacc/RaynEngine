package com.jgfx.engine.assets.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jgfx.assets.data.AssetData;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Represents the backing data for a texture
 */
public class TextureData implements AssetData {
    @Getter
    private int width, height;
    @Getter
    private int target;
    @Getter
    private int internalFormat, format;
    @Getter
    private Map<Integer, List<Integer>> textureParameters = Maps.newHashMap();
    @Getter
    private ByteBuffer data;

    public TextureData(int width, int height, int target, int internalFormat, int format, ByteBuffer data) {
        this.width = width;
        this.height = height;
        this.target = target;
        this.internalFormat = internalFormat;
        this.format = format;
        this.data = data;
    }

    /**
     * Adds a parameter
     *
     * @param key    param key
     * @param values param values
     */
    public void addParameters(int key, int... values) {
        if (textureParameters.containsKey(key))
            for (var value : values)
                textureParameters.get(key).add(value);
        else {
            List<Integer> vals = Lists.newArrayList();
            for (var value : values)
                vals.add(value);
            textureParameters.put(key, vals);
        }
    }


}
