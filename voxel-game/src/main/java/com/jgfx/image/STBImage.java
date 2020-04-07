package com.jgfx.image;

import com.jgfx.engine.utils.IOUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.stb.STBImage.*;

/**
 * This class will use stb to load a image
 */
public class STBImage {
    @Getter
    @Setter
    private ByteBuffer buffer;
    @Getter
    @Setter
    private int width, height, comp;
    private Logger logger = LogManager.getLogger();
    @Getter
    private boolean alpha;

    /**
     * Load image from file
     */
    public STBImage(File file) {
        loadImage(file);
        //If png we have alpha if jpeg we dont
        this.alpha = FilenameUtils.isExtension(file.getName(), "png");
    }


    /**
     * Used for the atlas
     */
    public STBImage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Loads an image from file
     */
    private void loadImage(File file) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Prepare image buffers */
            var w = stack.mallocInt(1);
            var h = stack.mallocInt(1);
            var comp = stack.mallocInt(1);

            /* Load image */
            stbi_set_flip_vertically_on_load(true);
            this.buffer = stbi_load(file.getPath(), w, h, comp, 4);
            if (buffer == null) {
                throw new RuntimeException("Failed to load a texture file!"
                        + System.lineSeparator() + stbi_failure_reason());
            }
            /* Get width and height of image */
            this.width = w.get();
            this.height = h.get();
            this.comp = comp.get();
            logger.debug("Loaded image {} from file with stb", file.getName());
        }
    }


    @Override
    public String toString() {
        return "GLImage{" +
                "width=" + width +
                ", height=" + height +
                ", data=" + StandardCharsets.UTF_8.decode(buffer).toString() +
                '}';
    }
}
