package com.jgfx.tiles;

import com.jgfx.assets.files.AbstractAssetFileFormat;
import com.jgfx.assets.files.AssetDataFile;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.image.STBImage;

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

    /**
     * @param urn    The urn identifying the asset being loaded.
     * @param inputs The inputs corresponding to this asset
     * @return returns the loaded tile data from file
     */
    @Override
    public TileData load(ResourceUrn urn, List<AssetDataFile> inputs) {
        var glImage = new STBImage(inputs.get(0).getPath().toFile());
        return new TileData(glImage);
    }

}
