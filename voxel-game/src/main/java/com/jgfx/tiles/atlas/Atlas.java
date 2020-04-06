package com.jgfx.tiles.atlas;

import com.google.common.collect.Lists;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.tiles.Tile;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.engine.assets.texture.TextureData;
import com.jgfx.engine.utils.JMath;
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
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;

/**
 * Represents a generic atlas. There should be an atlas for blocks, items, entities, and gui elements.
 */
public class Atlas {
    private static final int MAX_TILES = 65536;

    private final Logger logger;
    private final TObjectIntMap<ResourceUrn> tileIndexes;
    private int numFrames;
    @Getter private final List<Tile> tiles;
    @Getter private final int maxAtlasSize;
    @Getter private int atlasSize;
    @Getter private int tileSize;

    /**
     * Creates a new atlas with the given size, and tile size
     */
    public Atlas(int maxAtlasSize) {
        this.maxAtlasSize = maxAtlasSize;
        this.logger = LogManager.getLogger(Atlas.class);
        this.tiles = Lists.newArrayList();
        this.tileIndexes = new TObjectIntHashMap<>();
        this.numFrames = 0;
        this.atlasSize = 256;
        this.tileSize = 32;
    }

    /**
     * Creates a new atlas with the default max size of 4096px X 4096px
     */
    public Atlas() {
        this(4096);
    }

    /**
     * @return returns the computed texture coordinate for the given id
     */
    public Vector2f getTexCoords(int id) {
        var tilesPerDim = atlasSize / tileSize;
        return new Vector2f((id % tilesPerDim) * getRelativeTileSize(), (id / tilesPerDim) * getRelativeTileSize());
    }

    /**
     * @return returns the tile index for the given urn
     */
    private int getTileIndex(ResourceUrn urn) {
        if (tileIndexes.containsKey(urn))
            return tileIndexes.get(urn);
        logger.warn("Tile {} couldn't be resolved!", urn.toString());
        return 0;
    }

    /**
     * Builds the actual texture atlas!
     */
    public void buildAtlas() {
        Assets.list(Tile.class).forEach(this::indexTile);
        computeSizes();
        var numMipMaps = getNumMipmaps();
        var data = computeAtlasMipmaps(numMipMaps, new Color(0, 0, 0, 0), "terrain");
        for (int level = 0; level < data.length; level++) {
            var textureData = new TextureData(atlasSize, atlasSize, GL_TEXTURE_2D, GL_RGBA8, GL_RGBA, data[level]);
            textureData.addParameters(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            textureData.addParameters(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
            textureData.addParameters(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            textureData.addParameters(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            Assets.generateAsset(new ResourceUrn("engine:terrain#" + level), textureData, Texture.class);
        }
    }

    /**
     * @return returns the tile data, with the first 2 values being the x start, and y start. And the last two values being the stop
     */
    public Vector4f getTileData(ResourceUrn urn) {
        var index = getTileIndex(urn);
        var size = getRelativeTileSize();
        var coords = getTexCoords(index);
        return new Vector4f(coords.x, coords.y, coords.x + size, coords.y + size);
    }

    /**
     * @return returns the tile data for the given urn
     */
    public Vector4f getTileData(String name) {
        return getTileData(new ResourceUrn(name));
    }


    /**
     * @return returns the computed array of buffers contains the computed atlas with the index
     * of the buffer being the mipmap level
     */
    private ByteBuffer[] computeAtlasMipmaps(int numMipMaps, Color clearColor, String screenshotName) {
        var data = new ByteBuffer[numMipMaps];
        for (int i = 0; i < numMipMaps; i++) {
            var image = generateAtlas(i, clearColor);
            try (OutputStream stream = new BufferedOutputStream(Files.newOutputStream(Paths.get("voxel-game/src/main/resources/screenshots/" + screenshotName + "_" + i + ".png")))) {
                ImageIO.write(image, "png", stream);
            } catch (IOException e) {
                logger.warn("Failed to write atlas");
            }
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", bos);
                var decoder = new PNGDecoder(new ByteArrayInputStream(bos.toByteArray()));
                var buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
                decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
                buf.flip();
                data[i] = buf;
            } catch (IOException e) {
                logger.error("Failed to create atlas texture");
            }
        }
        return data;
    }

    /**
     * @return returns the generated atlas at the given mipmap level with the correct clear color
     */
    private BufferedImage generateAtlas(int mipMapLevel, Color clearColor) {
        var size = atlasSize / (1 << mipMapLevel);
        var textureSize = tileSize / (1 << mipMapLevel);
        var tilesPerDim = atlasSize / tileSize;

        var result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        var graphics = result.getGraphics();

        graphics.setColor(clearColor);
        graphics.fillRect(0, 0, size, size);

        var totalIndex = 0;
        for (var tile : tiles) {
            if (tile == null)
                totalIndex++;
            else {
                for (int frameIndex = 0; frameIndex < tile.getLength(); frameIndex++) {
                    var posX = totalIndex % tilesPerDim;
                    var posY = totalIndex / tilesPerDim;
                    graphics.drawImage(tile.getImage(frameIndex).getScaledInstance(textureSize, textureSize, Image.SCALE_SMOOTH), posX * textureSize, posY * textureSize, null);
                    totalIndex++;
                }
            }
        }
        result.flush();
        graphics.dispose();
        return result;
    }

    /**
     * Computes the correct atlas and tile size based upon the following:
     * 1.   The overall tile size is the size of the largest tile loaded
     * 2.   The atlas will never be larger than 4096*4096 px
     * 3.   The tile size gets adjusted if the tiles won't fit into the atlas using the overall tile size
     * 4.   The size of the atlas is always a power of two - as is the tile size
     */
    private void computeSizes() {
        tiles.stream().filter(tile -> tile.getImage(0).getWidth() > tileSize).forEach(tile -> tileSize = tile.getImage(0).getWidth());

        atlasSize = 1;
        while (atlasSize * atlasSize < numFrames) {
            atlasSize *= 2;
        }
        atlasSize = atlasSize * tileSize;

        if (atlasSize > maxAtlasSize) {
            atlasSize = maxAtlasSize;
            int maxTiles = (atlasSize / tileSize) * (atlasSize / tileSize);
            while (maxTiles < numFrames) {
                tileSize >>= 1;
                maxTiles = (atlasSize / tileSize) * (atlasSize / tileSize);
            }
        }
    }


    /**
     * Computes the index for a given tile
     */
    private void indexTile(ResourceUrn urn) {
        var maybeTile = Assets.get(urn, Tile.class);
        if (maybeTile.isPresent()) {
            var tile = maybeTile.get();
            if (numFrames + tile.getLength() > MAX_TILES) {
                logger.error("Maximum tiles exceeded when adding tile {}", urn.toString());
                return;
            }
            if (isValidTile(tile)) {
                tiles.add(tile);
                var index = numFrames;
                numFrames += tile.getLength();
                tileIndexes.put(urn, index);
            } else
                logger.error("Invalid tile {}, must be square with power of two sides.", urn.toString());
        }
    }

    /**
     * @return returns true if the tile is valid, and the size is a power of 2 (square tile)
     */
    private boolean isValidTile(Tile tile) {
        for (int i = 0; i < tile.getLength(); i++) {
            if (tile.getImage(i).getWidth() != tile.getImage(i).getHeight()
                    || !JMath.isPowerOfTwo(tile.getImage(i).getWidth())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return returns the computed number of mipmaps based upon the tile size
     */
    public int getNumMipmaps() {
        return JMath.sizeOfPower(tileSize) + 1;
    }

    /**
     * @return returns the computed relative tile size
     */
    public float getRelativeTileSize() {
        return ((float) tileSize / (float) atlasSize);
    }
}
