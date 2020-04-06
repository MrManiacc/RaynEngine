package com.jgfx.engine.assets.config;

import com.jgfx.assets.files.AbstractAssetFileFormat;
import com.jgfx.assets.files.AssetDataFile;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.texture.TextureData;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigFormat extends AbstractAssetFileFormat<ConfigData> {
    public ConfigFormat() {
        super("cfg", "ini");
    }

    @Override
    public ConfigData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        var configs = new ArrayList<Wini>();
        inputs.forEach(assetDataFile -> {
            try {
                configs.add(new Wini(assetDataFile.getPath().toFile()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return new ConfigData(configs);
    }

}
