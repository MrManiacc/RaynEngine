package com.jgfx.engine.assets.fbo;

import com.jgfx.assets.files.AbstractAssetFileFormat;
import com.jgfx.assets.files.AssetDataFile;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.config.ConfigData;
import org.ini4j.Wini;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * We arent reading fbos from file at this time
 */
public class FboFormat extends AbstractAssetFileFormat<FboData> {
    public FboFormat() {
        super("fbo");
    }

    @Override
    public FboData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        return FboData.EMPTY;
    }

}
