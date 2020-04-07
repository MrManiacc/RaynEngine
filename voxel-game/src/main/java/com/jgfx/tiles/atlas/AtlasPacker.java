package com.jgfx.tiles.atlas;

import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.engine.assets.texture.TextureData;
import com.jgfx.image.STBImage;
import com.jgfx.tiles.Tile;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;

/**
 * This class will take a group of {@link com.jgfx.image.STBImage} and create an atlas
 */
public final class AtlasPacker {
    private STBRPNode.Buffer nodes;
    private STBRPRect.Buffer rects;
    private List<Tile> tiles;
    private STBImage atlas;
    private static final int MAX_SIZE = 4096;
    private static final int MIN_SIZE = 32;
    private static final int MAX_IMAGES = 20000;

    public AtlasPacker(List<Tile> tiles) {
        this.tiles = tiles;
        this.nodes = STBRPNode.create(MAX_SIZE);
        this.rects = STBRPRect.create(MAX_IMAGES);
        this.atlas = new STBImage(MIN_SIZE, MIN_SIZE);
    }

    /**
     * Packs the images together
     */
    private void pack(STBRPContext context) {
        packRects(context);
        for (var i = 0; i < tiles.size(); i++) {
            var rect = rects.get(i);
            if (!rect.was_packed()) {
                if (atlas.getHeight() < atlas.getWidth())
                    atlas.setHeight(atlas.getWidth());
                else
                    atlas.setWidth(atlas.getWidth() * 2);
                pack(context);
            }
        }
    }

    /**
     * This will generate the atlas
     */
    public void generate() {
        var context = STBRPContext.create();
        pack(context);
        mapCoords();
        copy();
    }

    /**
     * Maps the correct texture coordinates
     */
    private void mapCoords() {
        for (var i = 0; i < tiles.size(); i++) {
            var tile = tiles.get(i);
            var rect = rects.get(i);
            var scale = (float) atlas.getWidth();
            tile.setCoords(new Vector4f(rect.x() / scale, rect.y() / scale, (rect.x() + rect.w()) / scale, (rect.y() + rect.h()) / scale));
        }
    }

    /**
     * This method will blit the images to the correct spots
     */
    private void copy() {
        var size = Math.max(atlas.getWidth(), atlas.getHeight());
        atlas.setWidth(size);
        atlas.setHeight(size);
        var data = BufferUtils.createByteBuffer(4 * atlas.getWidth() * atlas.getHeight());
        atlas.setBuffer(data);
        for (var i = tiles.size() - 1; i >= 0; i--) {
            var rect = rects.get(i);
            var image = tiles.get(i);
            blit(image.getImage(), rect.x(), rect.y());
        }
    }

    /**
     * This will write the input image to the atlas
     */
    private void blit(STBImage input, int x, int y) {
        var dst = atlas.getBuffer();
        var src = input.getBuffer();
        var pow = 4;
        x *= pow;
        y *= pow;
        for (int i = 0; i < input.getHeight() * pow; i += 4) {
            int dy = y + i;
            if (dy < 0 || dy > atlas.getHeight() * pow) continue;
            for (int j = 0; j < input.getWidth() * pow; j++) {
                int dx = x + j;
                if (dx < 0 || (dx > atlas.getWidth() * pow)) continue;
                var srcIndex = (i * input.getWidth() + j);
                var dstIndex = dx + dy * atlas.getWidth();
                if (dstIndex + 3 >= dst.capacity() || srcIndex + 3 >= src.capacity())
                    continue;
                if (dst.get(dstIndex) == 0) {
                    dst.put((dstIndex), src.get(srcIndex));
                    dst.put((dstIndex + 1), src.get(srcIndex + 1));
                    dst.put((dstIndex + 2), src.get(srcIndex + 2));
                    dst.put((dstIndex + 3), src.get(srcIndex + 3));
                }
            }
        }
    }

    /**
     * Packs the rects using the context
     */
    private void packRects(STBRPContext context) {
        for (var i = 0; i < tiles.size(); i++) {
            var image = tiles.get(i).getImage();
            var rect = rects.get(i);
            rect.id(i);
            rect.w((short) image.getWidth());
            rect.h((short) image.getHeight());
            if (rect.w() > atlas.getWidth())
                atlas.setWidth(roundUp(rect.w()));
            if (rect.h() > atlas.getHeight())
                atlas.setHeight(roundUp(rect.h()));
        }
        STBRectPack.stbrp_init_target(context, atlas.getWidth(), atlas.getHeight(), nodes);
        STBRectPack.stbrp_pack_rects(context, rects);
    }

    /**
     * @return returns the next number that is power of 2
     */
    private int roundUp(int n) {
        int x = 1;
        while (x < n) x <<= 1;
        return x;
    }

    /**
     * Writes the atlas to file
     */
    public void toFile(File file) {
        STBImageWrite.stbi_write_png(file.getPath(), atlas.getWidth(), atlas.getHeight(), tiles.get(0).getImage().getComp(), atlas.getBuffer(), 0);
    }

    /**
     * Generates a textures for the given urn of the atlas
     */
    public void toTexture(ResourceUrn urn) {
        var textureData = new TextureData(atlas.getWidth(), atlas.getHeight(), GL_TEXTURE_2D, GL_RGBA8, GL_RGBA, this.atlas.getBuffer());
        textureData.addParameters(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        textureData.addParameters(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        textureData.addParameters(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        textureData.addParameters(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        Assets.generateAsset(urn, textureData, Texture.class);
    }
}
