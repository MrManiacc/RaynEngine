package com.jgfx.tiles;

import com.jgfx.assets.data.AssetData;
import com.jgfx.image.STBImage;
import lombok.Getter;

import java.awt.image.BufferedImage;

/**
 * Stores the data about a given tile.
 */
public class TileData implements AssetData {
    @Getter
    private STBImage image;

    public TileData(STBImage image) {
        this.image = image;
    }

}
