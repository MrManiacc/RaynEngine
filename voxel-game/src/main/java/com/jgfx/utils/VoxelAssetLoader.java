package com.jgfx.utils;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.type.AssetTypeManager;
import com.jgfx.blocks.Block;
import com.jgfx.tiles.atlas.Atlas;
import com.jgfx.blocks.load.BlockFormat;
import com.jgfx.tiles.Tile;
import com.jgfx.tiles.TileFormat;
import com.jgfx.engine.assets.config.Config;
import com.jgfx.engine.assets.config.ConfigFormat;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.assets.shader.ShaderFormat;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.engine.assets.texture.TextureFormat;
import com.jgfx.engine.defaults.AssetLoad;

/**
 * This class override the asset loader, and injects a block loader, because this is game specific.
 */
public class VoxelAssetLoader extends AssetLoad {
    /**
     * Register the asset type resolvers
     *
     * @param typeManager the type manager
     */
    protected void registerAssetResolvers(AssetTypeManager typeManager) {
        super.registerAssetResolvers(typeManager);
        typeManager.registerAssetType(Tile.class, Tile::new, new TileFormat(), "tiles", "paintings");
        typeManager.registerAssetType(Block.class, Block::new, new BlockFormat(), "blocks");
    }

    /**
     * Here we load the world atlas
     */
    @Override
    protected void postInitialize() {
        var atlas = CoreContext.put(new Atlas(4096));
        atlas.buildAtlas();
    }
}
