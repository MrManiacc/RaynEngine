package com.jgfx.tiles.atlas;

import com.google.common.collect.Lists;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.image.STBImage;
import com.jgfx.tiles.Tile;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.texture.TextureData;
import com.jgfx.engine.utils.JMath;
import com.jgfx.tiles.atlas.events.TextureThreadedAsset;
import com.jgfx.utils.ThreadedAssetGenerator;
import de.matthiasmann.twl.utils.PNGDecoder;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.joml.Vector4f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;

/**
 * Represents a generic atlas. There should be an atlas for blocks, items, entities, and gui elements.
 */
public class Atlas {
    private static final int MAX_TILES = 65536;

    private final Logger logger;
    @Getter
    private final List<Tile> tiles;
    @Getter
    private final int maxAtlasSize;
    @Getter
    private int atlasSize;
    @Getter
    private int tileSize;

    /**
     * Creates a new atlas with the given size, and tile size
     */
    public Atlas(int maxAtlasSize) {
        this.maxAtlasSize = maxAtlasSize;
        this.logger = LogManager.getLogger(Atlas.class);
        this.tiles = Lists.newArrayList();
        this.atlasSize = 256;
        this.tileSize = 32;
    }

    /**
     * Builds the actual texture atlas!
     */
    public void buildAtlas() {
        Assets.list(Tile.class).forEach(this::indexTile);
        var numMipMaps = getNumMipmaps();
        AtlasPacker packer = new AtlasPacker(tiles);
        packer.generate();
        packer.toFile(new File("voxel-game/src/main/resources/screenshots/output.png"));
        packer.toTexture(new ResourceUrn("engine:terrain#0"));
    }

    /**
     * Computes the index for a given tile
     */
    private void indexTile(ResourceUrn urn) {
        var maybeTile = Assets.get(urn, Tile.class);
        if (maybeTile.isPresent()) {
            var tile = maybeTile.get();
            if (isValidTile(tile)) {
                tiles.add(tile);
            } else
                logger.error("Invalid tile {}, must be square with power of two sides.", urn.toString());
        }
    }

    /**
     * @return returns true if the tile is valid, and the size is a power of 2 (square tile)
     */
    private boolean isValidTile(Tile tile) {
        if (tile.getImage().getWidth() != tile.getImage().getHeight()
                || !JMath.isPowerOfTwo(tile.getImage().getWidth())) {
            return false;
        }
        return true;
    }

    /**
     * @return returns the computed number of mipmaps based upon the tile size
     */
    public int getNumMipmaps() {
        return JMath.sizeOfPower(tileSize) + 1;
    }
}
