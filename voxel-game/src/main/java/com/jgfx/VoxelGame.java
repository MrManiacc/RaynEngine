package com.jgfx;

import com.jgfx.assets.naming.Name;
import com.jgfx.assets.naming.Version;
//import com.jgfx.gui.JgfxGui;
import com.jgfx.gui.JgfxGui;
import com.jgfx.utils.ThreadedAssetGenerator;
import com.jgfx.utils.VoxelAssetLoader;
import com.jgfx.engine.ecs.EngineSubsystem;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.Game;
import com.jgfx.engine.game.GameEngine;
import com.jgfx.engine.game.GameRunException;
import com.jgfx.engine.game.GameRunner;
import com.jgfx.engine.glfw.GlfwInputSubsystem;
import com.jgfx.engine.glfw.GlfwWindowSubsystem;
import com.jgfx.engine.load.LoadProcess;
import com.jgfx.player.load.PlayerLoader;

import java.util.List;

@Game(name = "voxel-game", version = "1.0.0")
public class VoxelGame extends GameEngine {

    public VoxelGame(Name name, Version version, List<EngineSubsystem> subsystems, List<EntitySystem> entitySystems, List<LoadProcess> loadProcesses) {
        super(name, version, subsystems, entitySystems, loadProcesses);
    }

    /**
     * Here we want to add our default window subsystem
     */
    @Override
    protected void preInitialization() {
        addSubsystem(new GlfwWindowSubsystem());
        addSubsystem(new GlfwInputSubsystem());
        addSubsystem(new JgfxGui());
        addLoadProcess(new VoxelAssetLoader());
        addLoadProcess(new PlayerLoader());
    }


    public static void main(String[] args) throws GameRunException {
        GameRunner.run(VoxelGame.class);
    }
}
