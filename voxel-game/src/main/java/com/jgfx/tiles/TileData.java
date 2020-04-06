package com.jgfx.tiles;

import com.jgfx.assets.data.AssetData;

import java.awt.image.BufferedImage;

/**
 * Stores the data about a given tile.
 */
public class TileData implements AssetData {
    private BufferedImage[] images;

    public TileData(BufferedImage[] images) {
        this.images = images;
    }

    public BufferedImage[] getImages() {
        return images;
    }
}
