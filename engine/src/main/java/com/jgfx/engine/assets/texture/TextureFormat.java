package com.jgfx.engine.assets.texture;

import com.jgfx.assets.files.AbstractAssetFileFormat;
import com.jgfx.assets.files.AssetDataFile;
import com.jgfx.assets.urn.ResourceUrn;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.stb.STBImage.*;

/**
 * Creates a texture format, to load the asset data from the input
 */
public class TextureFormat extends AbstractAssetFileFormat<TextureData> {
    public TextureFormat() {
        super("png", "jpg", "jpeg");
    }

    /**
     * Loads the image byte buffer data
     *
     * @param urn    The urn identifying the asset being loaded.
     * @param inputs The inputs corresponding to this asset
     * @return returns the parsed texture data
     * @throws IOException throws exception if the file isn't found
     */
    @Override
    public TextureData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        var path = inputs.get(0).getPath().toString();
        ByteBuffer image;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            /* Load image */
            stbi_set_flip_vertically_on_load(true);
            image = stbi_load(path, w, h, comp, 4);
            if (image == null) {
                throw new IOException("Failed to load a texture file!"
                        + System.lineSeparator() + stbi_failure_reason());
            }

            /* Get width and height of image */
            width = w.get();
            height = h.get();
        }
        var textureData = new TextureData(width, height, GL_TEXTURE_2D, GL_RGBA8, GL_RGBA, image);
        textureData.addParameters(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        textureData.addParameters(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        textureData.addParameters(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        textureData.addParameters(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        return textureData;
    }
}
