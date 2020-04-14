package com.jgfx.engine.assets.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jgfx.assets.data.AssetData;
import lombok.Getter;
import org.lwjgl.BufferUtils;

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

    public void flip() {
        var srcPixels = new byte[data.capacity()];
        data.get(srcPixels);
        var rotated = rotate(srcPixels, width, height);
        var dest = BufferUtils.createByteBuffer(data.capacity());
        for (var b : rotated)
            dest.put(b);
        this.data = dest;
    }

    /**
     * @return returns the image as a rotated byte array. This will rotate the blocks so they're orientated correctly
     */
    private byte[] rotate(byte[] data, int width, int height) {
        int srcPos = 0; // We can just increment this since the data pack order matches our loop traversal: left to right, top to bottom. (Just like reading a book.)
        var destPixels = new byte[data.length];
        for (int srcY = 0; srcY < height; srcY++) {
            for (int srcX = 0; srcX < width; srcX++) {
                int destX = ((height - 1) - srcY);
                int destPos = (((srcX * width) + destX) * 4);
                if (destPos >= destPixels.length)
                    continue;
                destPixels[destPos++] = data[srcPos++];    // alpha
                destPixels[destPos++] = data[srcPos++];        // blue
                destPixels[destPos++] = data[srcPos++];        // green
                destPixels[destPos++] = data[srcPos++];        // red
            }
        }
        return destPixels;
    }


}
