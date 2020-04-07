package com.jgfx.tiles;

import com.jgfx.assets.Asset;
import com.jgfx.assets.type.AssetType;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.image.STBImage;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4d;
import org.joml.Vector4f;

import java.awt.image.BufferedImage;

/**
 * Represents a generic image/tile that can be loaded into an atlas
 */
public class Tile extends Asset<TileData> {
    @Getter
    private STBImage image;
    @Getter
    @Setter
    private Vector4f coords;

    /**
     * The constructor for an asset. It is suggested that implementing classes provide a constructor taking both the urn, and an initial AssetData to load.
     *
     * @param urn       The urn identifying the asset.
     * @param assetType The asset type this asset belongs to.
     */
    public Tile(ResourceUrn urn, AssetType<?, TileData> assetType, TileData data) {
        super(urn, assetType);
        reload(data);
    }

    /**
     * Reloads the tile from the given data
     *
     * @param data The data to load.
     */
    @Override
    public void reload(TileData data) {
        this.image = data.getImage();
    }


}
