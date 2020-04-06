package com.jgfx.assets.type;

import com.google.common.collect.Sets;
import com.jgfx.assets.Asset;
import com.jgfx.assets.data.AssetData;
import com.jgfx.assets.urn.ResourceUrn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * AssetManager provides an simplified interface for working with assets across multiple asset types.
 * <p>
 * To do this it uses an {@link AssetTypeManager} to obtain the AssetTypes relating to an Asset
 * class of interest, and delegates down to them for actions such as obtaining and reloading assets.
 * </p>
 *
 * @author Immortius
 */
public class AssetManager {
    private final AssetTypeManager assetTypeManager;
    private final Logger logger;

    public AssetManager(AssetTypeManager assetTypeManager) {
        this.assetTypeManager = assetTypeManager;
        this.logger = LogManager.getLogger();
    }

    /**
     * Retrieves an asset with the given urn and type
     *
     * @param urn  The urn of the asset to retrieve
     * @param type The type of asset to retrieve
     * @param <T>  The class of Asset
     * @param <U>  The class of AssetData
     * @return An Optional containing the requested asset if successfully obtained
     */
    public <T extends Asset<U>, U extends AssetData> Optional<T> getAsset(ResourceUrn urn, Class<T> type) {
        var assetType = assetTypeManager.getAssetType(type);
        if (!assetType.isPresent())
            return Optional.empty();
        return assetType.get().getAsset(urn);
    }

    /**
     * Retrieves an asset with the given urn and type
     *
     * @param urn  The urn of the asset to retrieve
     * @param type The type of asset to retrieve
     * @param <T>  The class of Asset
     * @param <U>  The class of AssetData
     * @return An Optional containing the requested asset if successfully obtained
     */
    public <T extends Asset<U>, U extends AssetData> Optional<T> getAsset(String urn, Class<T> type) {
        return getAsset(new ResourceUrn(urn), type);
    }

    /**
     * Creates or reloads an asset with the given urn, data and type. The type must be the actual type of the asset, not a super type.
     *
     * @param urn  The urn of the asset
     * @param data The data to load the asset with
     * @param type The type of the asset
     * @param <T>  The class of Asset
     * @param <U>  The class of AssetData
     * @return The loaded asset
     * @throws java.lang.IllegalStateException if the asset type is not managed by this AssetManager.
     */
    public <T extends Asset<U>, U extends AssetData> T loadAsset(ResourceUrn urn, U data, Class<T> type) {
        Optional<AssetType<T, U>> assetType = assetTypeManager.getAssetType(type);
        if (assetType.isPresent()) {
            return assetType.get().loadAsset(urn, data);
        } else {
            throw new IllegalStateException(type + " is not a supported type of asset");
        }
    }

    /**
     * Retrieves a set of the ResourceUrns for all available assets of the given Asset class (including subtypes). An available asset is either a loaded asset, or one
     * which can be requested. The set is not necessarily complete as assets procedurally generated from their resource urn may not be included.
     *
     * @param type The Asset class of interest
     * @param <T>  The Asset class
     * @return A set of the ResourceUrns of all available assets
     */
    public <T extends Asset<U>, U extends AssetData> Set<ResourceUrn> getAvailableAssets(Class<T> type) {
        var assetType = assetTypeManager.getAssetType(type);
        if (assetType.isPresent())
            return assetType.get().getLoadedAssetUrns();
        logger.error("Failed to get AssetType for type {}", type.getSimpleName());
        return new HashSet<>();
    }


}
