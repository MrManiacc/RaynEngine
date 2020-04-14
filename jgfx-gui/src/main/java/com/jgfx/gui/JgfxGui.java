package com.jgfx.gui;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.ecs.World;
import com.jgfx.engine.ecs.component.SingleComponent;
import com.jgfx.engine.event.Bus;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.assets.fbo.Fbo;
import com.jgfx.engine.assets.fbo.FboData;
import com.jgfx.gui.components.camera.OrthoCameraCmp;
import com.jgfx.gui.elements.containers.Root;
import com.jgfx.gui.systems.SizeResolver;
import lombok.SneakyThrows;

/**
 * The main entry for the gui
 */
public class JgfxGui implements EngineSubsystem {
    private boolean loaded;
    @In World world;

    /**
     * We want to registers to all GUI events manually
     */
    @SneakyThrows
    @Override
    public void preInitialise() {
        Bus.GUI.register(this);
        CoreContext.put(this);
        CoreContext.put(new Root());
        loaded = true;

    }

    @Override
    public void initialise() {
        createFbos();
        world.addSystem(new SizeResolver());
        world.createEntity(new OrthoCameraCmp(), new SingleComponent("engine:entities#camera-2d"));
    }


    /**
     * This will generate our fbos
     */
    private void createFbos() {
        Assets.generateAsset(new ResourceUrn("engine", "fbos", "scene"), new FboData(FboData.DEPTH_TEXTURE_ATTACHMENT, false, 4), Fbo.class);
        Assets.generateAsset(new ResourceUrn("engine", "fbos", "effects"), new FboData(FboData.DEPTH_TEXTURE_ATTACHMENT, false, 4), Fbo.class);
    }

    @Override
    public String getName() {
        return "JgfxGui";
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
