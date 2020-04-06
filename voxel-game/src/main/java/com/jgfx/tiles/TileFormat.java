package com.jgfx.tiles;

import com.jgfx.assets.files.AbstractAssetFileFormat;
import com.jgfx.assets.files.AssetDataFile;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.utils.JMath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Loads a block tile from file
 */
public class TileFormat extends AbstractAssetFileFormat<TileData> {
    public TileFormat() {
        super("png");
    }

    @Override
    public TileData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        try (InputStream stream = inputs.get(0).openStream()) {
            BufferedImage image = ImageIO.read(stream);
            if (!JMath.isPowerOfTwo(image.getHeight()) || image.getWidth() % image.getHeight() != 0 || image.getWidth() == 0)
                throw new IOException("Invalid tile - must be horizontal row of power-of-two sized squares");
            var frames = new BufferedImage[image.getWidth() / image.getHeight()];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = new BufferedImage(image.getHeight(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                var graphics = frames[i].createGraphics();
                graphics.drawImage(image, -image.getHeight() * i, 0, null);
                graphics.dispose();
            }
            image.flush();
            image.getGraphics().dispose();
            stream.close();
            return new TileData(frames);
        }
    }

}
