package com.jgfx.assets.files;

import com.jgfx.assets.data.AssetData;
import com.jgfx.assets.urn.ResourceUrn;

import java.io.IOException;
import java.util.List;

/**
 * An AssetFileFormat handles loading a file representation of an asset into the appropriate
 *
 * @author Immortius
 */
public interface AssetFileFormat<T extends AssetData> extends FileFormat {

    /**
     * @param urn    The urn identifying the asset being loaded.
     * @param inputs The inputs corresponding to this asset
     * @return The loaded asset
     * @throws IOException If there are any errors loading the asset
     */
    T load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException;
}
