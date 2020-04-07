package com.jgfx.tiles.atlas.events;

import com.jgfx.assets.Asset;
import com.jgfx.assets.data.AssetData;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.texture.TextureData;

/**
 * This is used so we can generate the textures on the main thread
 */
public class TextureThreadedAsset {
    public final ResourceUrn urn;
    public final TextureData assetData;

    public TextureThreadedAsset(ResourceUrn urn, TextureData assetData) {
        this.urn = urn;
        this.assetData = assetData;
    }

}
