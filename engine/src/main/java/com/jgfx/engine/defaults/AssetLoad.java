package com.jgfx.engine.defaults;

import com.google.common.collect.Queues;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.files.AssetSourceResolver;
import com.jgfx.assets.type.AssetManager;
import com.jgfx.assets.type.AssetType;
import com.jgfx.assets.type.AssetTypeManager;
import com.jgfx.engine.assets.config.Config;
import com.jgfx.engine.assets.config.ConfigFormat;
import com.jgfx.engine.assets.fbo.Fbo;
import com.jgfx.engine.assets.fbo.FboFormat;
import com.jgfx.engine.assets.shader.Shader;
import com.jgfx.engine.assets.shader.ShaderFormat;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.engine.assets.texture.TextureFormat;
import com.jgfx.engine.load.SingleStepLoadProcess;

import java.util.Queue;

/**
 * This class will load the assets
 */
public class AssetLoad extends SingleStepLoadProcess {
    private Queue<AssetSourceResolver> unresolved = Queues.newArrayDeque();
    private AssetTypeManager typeManager;

    public AssetLoad() {
        super("Loading assets");
    }

    public AssetLoad(String message) {
        super(message);
    }

    /**
     * Registers the asset resolver things
     */
    private void initialize() {
        typeManager = new AssetTypeManager();
        registerAssetResolvers(typeManager);
        CoreContext.put(new AssetManager(typeManager));
        resolveAssets();
    }

    /**
     * Attempts to load the next source
     */
    private void nextUnresolved() {
        if (this.unresolved.isEmpty()) return;
        var unresolved = this.unresolved.poll();
        if (unresolved == null) return;
        if (!unresolved.resolveAssets()) {
            //If it's unresolved again we put it to the back of the queue
            this.unresolved.add(unresolved);
        }
    }

    /**
     * Register the asset type resolvers
     *
     * @param typeManager the type manager
     */
    protected void registerAssetResolvers(AssetTypeManager typeManager) {
        typeManager.registerAssetType(Shader.class, Shader::new, new ShaderFormat(), "shaders");
        typeManager.registerAssetType(Texture.class, Texture::new, new TextureFormat(), "textures");
        typeManager.registerAssetType(Config.class, Config::new, new ConfigFormat(), "config");
        typeManager.registerAssetType(Fbo.class, Fbo::new, new FboFormat(), "fbos");
    }

    /**
     * Used to initialize anything else related to assets after the assets have been loaded
     */
    protected void postInitialize() {
    }

    /**
     * Resolves the asset file paths for the registered types
     */
    private void resolveAssets() {
        var assetSourceResolver = new AssetSourceResolver(typeManager);
        if (!assetSourceResolver.resolveAssets()) {
            this.unresolved.add(assetSourceResolver);
        }
    }


    /**
     * This method will load the asset manager and map the assets correctly
     *
     * @return returns true
     */
    @Override
    public boolean step() {
        if (typeManager == null)
            initialize();
        nextUnresolved();
        if (unresolved.isEmpty())
            postInitialize();
        return unresolved.isEmpty();
    }
}
