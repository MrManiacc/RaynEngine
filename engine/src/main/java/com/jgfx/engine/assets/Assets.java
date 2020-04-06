package com.jgfx.engine.assets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.jgfx.assets.Asset;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.data.AssetData;
import com.jgfx.assets.type.AssetManager;
import com.jgfx.assets.urn.ResourceUrn;

import java.util.Optional;
import java.util.Set;

/**
 * This class will allow for static bindings of assets.
 */
public class Assets {
    /**
     * @return The requested asset, or null if it doesn't exist.
     */
    public static <T extends Asset<U>, U extends AssetData> Optional<T> get(ResourceUrn urn, Class<T> type) {
        return CoreContext.get(AssetManager.class).getAsset(urn, type);
    }

    /**
     * @return The requested asset, or null if it doesn't exist.
     */
    public static <T extends Asset<U>, U extends AssetData> Optional<T> get(String urn, Class<T> type) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(urn));
        return CoreContext.get(AssetManager.class).getAsset(urn, type);
    }

    /**
     * @return Loads an asset from the given data, and returns the asset
     */
    public static <T extends Asset<U>, U extends AssetData> T generateAsset(ResourceUrn urn, U data, Class<T> assetClass) {
        return CoreContext.get(AssetManager.class).loadAsset(urn, data, assetClass);
    }

    /**
     * @return An set containing the urns of resources belonging to the given asset type
     */
    public static <T extends Asset<U>, U extends AssetData> Set<ResourceUrn> list(Class<T> type) {
        return CoreContext.get(AssetManager.class).getAvailableAssets(type);
    }

}
