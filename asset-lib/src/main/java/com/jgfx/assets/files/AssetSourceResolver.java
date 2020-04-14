package com.jgfx.assets.files;

import com.google.common.collect.Sets;
import com.jgfx.assets.Asset;
import com.jgfx.assets.type.AssetType;
import com.jgfx.assets.type.AssetTypeManager;
import com.jgfx.assets.data.AssetData;
import com.jgfx.assets.urn.ResourceUrn;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class will determine all of the files for a given asset type, and load the asset data by creating the asset file format
 */
public class AssetSourceResolver {
    private AssetTypeManager typeManager;
    //TODO: change this!!!!
    private static final String ASSETS_FOLDER = "Client/src/main/resources/engine";
    private final Set<ResourceUrn> resolvedAssets = Sets.newConcurrentHashSet();
    private final Set<AssetMetaData> unresolvedAssets = Sets.newConcurrentHashSet();

    public AssetSourceResolver(AssetTypeManager typeManager) {
        this.typeManager = typeManager;
    }

    /**
     * Resolves the assets for the given type
     *
     * @return returns false if there's unresolved assets
     */
    @SneakyThrows
    public boolean resolveAssets() {
        var allLoaded = true;

        //We want to load all of the unloaded assets first
        allLoaded = loadUnresolvedAssets();

        for (var assetClass : typeManager.getRegisteredTypes()) {
            var metaList = getMetaData(assetClass);
            if (metaList == null)
                throw new IOException("Failed to load asset type '" + assetClass.getName() + "'.");
            for (var meta : metaList) {
                if (!parseAsset(meta))
                    allLoaded = false;
            }
        }
        return allLoaded;
    }

    /**
     * Attempts to load all of the unresolved asset types
     *
     * @return returns false if any failed to load
     */
    private boolean loadUnresolvedAssets() {
        var allLoaded = true;
        for (var meta : unresolvedAssets) {
            if (!parseAsset(meta)) {
                allLoaded = false;
                System.out.println("failed to parse: " + meta.urn);
            }
        }
        return allLoaded;
    }

    /**
     * Extracts the asset meta data from the files before loading     *
     * @return returns the asset data
     */
    private List<AssetMetaData> getMetaData(Class<? extends Asset> assetClass) {
        var metaList = new ArrayList<AssetMetaData>();
        Optional<List<String>> assetFolder = typeManager.getAssetFolders(assetClass);
        Optional<AssetType<? extends Asset, ? extends AssetData>> assetType = typeManager.getAssetType(assetClass);
        Optional<AbstractAssetFileFormat<? extends AssetData>> assetFormat = typeManager.getFormat(assetClass);
        if (assetType.isPresent() && assetFormat.isPresent() && assetFolder.isPresent()) {
            var type = assetType.get();
            var format = assetFormat.get();
            var folders = assetFolder.get();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            var url = loader.getResource("assets");
            for (var folder : folders) {
                var assetsFolder = new File(Objects.requireNonNull(url).getPath());
                if (!assetsFolder.exists()) return null;
                var files = new File(assetsFolder, folder).listFiles();
                if (files != null && files.length > 0)
                    for (var file : Objects.requireNonNull(files)) {
                        var filePath = Paths.get(file.toURI());
                        if (format.getFileMatcher().matches(filePath)) {
                            var urn = new ResourceUrn("engine", folder, format.getAssetName(file.getName()).toLowerCase());
                            var assetDataFiles = Collections.singletonList(new AssetDataFile(filePath));
                            metaList.add(new AssetMetaData(assetDataFiles, format, type, urn));
                        }
                    }
            }
            return metaList;
        }
        return null;
    }

    /**
     * Parses the asset, or put's it into the unready map, which will attempt to load the asset every time
     * a different asset is loaded. This allows for us to make shaders dependent upon other shader
     *
     * @param meta the meta to use to load the asset
     * @return returns false if the asset was unresolved
     */
    private boolean parseAsset(AssetMetaData meta) {
        if (resolvedAssets.contains(meta.urn)) {
            unresolvedAssets.remove(meta);
            return true;
        }
        try {
            var data = meta.format.load(meta.urn, meta.files);
            if (data == null) {
                unresolvedAssets.add(meta);
            } else {
                if (meta.type.loadAsset(meta.urn, data) != null) {
                    unresolvedAssets.remove(meta);
                    resolvedAssets.add(meta.urn);
                    return true;
                }
            }
        } catch (IOException e) {
            unresolvedAssets.add(meta);
        }
        return false;
    }

    private static class AssetMetaData {
        public final List<AssetDataFile> files;
        public final AssetFileFormat<?> format;
        public final AssetType type;
        public final ResourceUrn urn;

        public AssetMetaData(List<AssetDataFile> files, AssetFileFormat<?> format, AssetType type, ResourceUrn urn) {
            this.files = files;
            this.format = format;
            this.type = type;
            this.urn = urn;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;

            if (object == null || getClass() != object.getClass()) return false;

            AssetMetaData that = (AssetMetaData) object;

            return new EqualsBuilder()
                    .append(urn, that.urn)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(urn)
                    .toHashCode();
        }
    }

}