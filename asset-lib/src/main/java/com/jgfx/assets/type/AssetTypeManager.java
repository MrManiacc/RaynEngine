package com.jgfx.assets.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.jgfx.assets.Asset;
import com.jgfx.assets.data.AssetData;
import com.jgfx.assets.data.AssetFactory;
import com.jgfx.assets.files.AbstractAssetFileFormat;
import com.jgfx.assets.urn.ResourceUrn;

import java.util.*;

/**
 * Maps a class to an give asset type
 */
public class AssetTypeManager {
    private final Map<Class<? extends Asset>, AssetType<?, ?>> assetTypes = Maps.newHashMap();
    private final Map<Class<? extends Asset>, String> assetFolders = Maps.newHashMap();
    private final Map<Class<? extends Asset>, AbstractAssetFileFormat<? extends AssetData>> assetFormat = Maps.newHashMap();

    /**
     * Registers an asset type. It will be available after the next time
     * read from modules from the provided subfolders. If there are no subfolders then assets will not be loaded from modules.
     *
     * @param type      The type of to register as a core type
     * @param factory   The factory to create assets of the desired type from asset data
     * @param subfolder The name of the subfolders which asset files related to this type will be read from within modules
     * @param <T>       The type of asset
     * @param <U>       The type of asset data
     */
    public synchronized <T extends Asset<U>, U extends AssetData> void registerAssetType(Class<T> type, AssetFactory<T, U> factory, AbstractAssetFileFormat<U> format, String subfolder) {
        Preconditions.checkState(!assetTypes.containsKey(type), "Asset type '" + type.getSimpleName() + "' already registered");
        assetFormat.put(type, format);
        AssetType<T, U> assetType = new AssetType<>(type, factory);
        this.assetTypes.put(type, assetType);
        this.assetFolders.put(type, subfolder);
    }

    /**
     * Gets the type of asset for the for the given class type
     *
     * @param type the type of asset to search for
     * @param <T>  The type of asset
     * @param <U>  The type of asset data
     * @return returns an asset type of null
     */
    public <T extends Asset<U>, U extends AssetData> Optional<AssetType<T, U>> getAssetType(Class<T> type) {
        return Optional.ofNullable((AssetType<T, U>) assetTypes.get(type));
    }

    /**
     * Gets an asset's folder based on the given asset type
     *
     * @param type the type of asset to get folder for
     * @param <T>  the type of asset
     * @param <U>  the type of asset data
     * @return returns the asset folder
     */
    public <T extends Asset<U>, U extends AssetData> Optional<String> getAssetFolder(Class<T> type) {
        return Optional.ofNullable(assetFolders.get(type));
    }

    /**
     * Gets the registered types for all of the assets
     *
     * @return returns collection of asset types
     */
    public Collection<Class<? extends Asset>> getRegisteredTypes() {
        return assetTypes.keySet();
    }

    /**
     * Gets the abstract asset format for using inside the asset source resolver
     *
     * @param type the type of asset
     * @param <T>  the generic type
     * @param <U>  the generic asset data type
     * @return returns the format
     */
    public <T extends Asset<U>, U extends AssetData> Optional<AbstractAssetFileFormat<U>> getFormat(Class<T> type) {
        return Optional.ofNullable((AbstractAssetFileFormat<U>) assetFormat.get(type));
    }


}
