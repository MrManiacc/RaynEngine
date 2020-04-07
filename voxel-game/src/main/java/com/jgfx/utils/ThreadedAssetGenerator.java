package com.jgfx.utils;

import com.google.common.collect.Queues;
import com.google.common.eventbus.Subscribe;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.data.AssetData;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.texture.Texture;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.injection.anotations.EventSubscriber;
import com.jgfx.engine.status.EngineStatusUpdatedEvent;
import com.jgfx.tiles.atlas.events.TextureThreadedAsset;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

/**
 * This class will handle generation of assets from the main thread
 */
@EventSubscriber
public class ThreadedAssetGenerator implements EngineSubsystem {
    @Getter
    private final String name = "Asset Generator";
    @Getter private boolean loaded = false;
    private Queue<TextureThreadedAsset> textureAssets = Queues.newArrayDeque();
    private final Logger logger = LogManager.getLogger();

    @Override
    public void preInitialise() {
        CoreContext.put(this);
        this.loaded = true;
    }

    /**
     * Create all of the assets on main thread
     */
    @Override
    public void postInitialise() {
        var next = textureAssets.peek();
        while(next != null){
            logger.debug("Generating [{}] on main thread", next.urn);
            Assets.generateAsset(next.urn, next.assetData, Texture.class);
            textureAssets.remove();
            next = textureAssets.peek();
        }
    }

    /**
     * Queues a texture asset to be generated from the main thread
     */
    public void queueTextureAsset(TextureThreadedAsset asset){
        textureAssets.add(asset);
        logger.debug("Queued [{}] for generation on main thread", asset.urn);
    }



}
