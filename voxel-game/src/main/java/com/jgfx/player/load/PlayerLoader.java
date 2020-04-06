package com.jgfx.player.load;

import com.jgfx.engine.assets.config.Config;
import com.jgfx.engine.ecs.World;
import com.jgfx.engine.ecs.component.SingleComponent;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Resource;
import com.jgfx.engine.load.SingleStepLoadProcess;
import com.jgfx.engine.window.IWindow;
import com.jgfx.player.data.PlayerCamera;
import com.jgfx.player.data.PlayerInfo;
import com.jgfx.player.data.PlayerTransform;
import org.joml.Vector3f;

/**
 * This class will load the local player
 */
//@AutoRegister
public class PlayerLoader extends SingleStepLoadProcess {
    @Resource("engine:config#player") private Config config;
    @In private World world;
    @In private IWindow window;

    public PlayerLoader() {
        super("Loading local player...");
    }

    @Override
    public boolean step() {
        var playerInfo = createPlayerInfo();
        var playerCamera = createPlayerCamera();
        var playerTransform = createPlayerTransform();
        world.createEntity(
                playerInfo, playerCamera, playerTransform, new SingleComponent("engine:entities#local-player")
        );
        return true;
    }

    /**
     * @return returns the player info, with the correct info loaded from the config file
     */
    private PlayerInfo createPlayerInfo() {
        var forward = config.getInt("input", "forward");
        var backward = config.getInt("input", "backward");
        var left = config.getInt("input", "left");
        var right = config.getInt("input", "right");
        var up = config.getInt("input", "up");
        var down = config.getInt("input", "down");
        var sprint = config.getInt("input", "sprint");
        var toggle = config.getInt("input", "toggle-camera");
        var verticalSens = config.getDouble("speed", "vertical-sensitivity");
        var horizontalSens = config.getDouble("speed", "horizontal-sensitivity");
        var movementSpeed = config.getDouble("speed", "movement-speed");
        var strafeSpeed = config.getDouble("speed", "strafe-speed");
        var sprintSpeed = config.getDouble("speed", "sprint-speed");
        return new PlayerInfo(forward, backward, left, right, up, down, sprint, toggle, verticalSens, horizontalSens, movementSpeed, strafeSpeed, sprintSpeed);
    }

    /**
     * @return returns the player camera which uses the fov from the player file, along with the window class
     * to create the aspect etc
     */
    private PlayerCamera createPlayerCamera() {
        var fov = config.getDouble("view", "fov");
        var near = config.getDouble("view", "near");
        var far = config.getDouble("view", "far");
        return new PlayerCamera((float) Math.toRadians(fov), window.getWidth() / window.getHeight(), (float) near, (float) far);
    }

    /**
     * @return returns the player transform from the saved position
     */
    private PlayerTransform createPlayerTransform() {
        var x = (float) config.getDouble("position", "x");
        var y = (float) config.getDouble("position", "y");
        var z = (float) config.getDouble("position", "z");
        return new PlayerTransform(new Vector3f(x, y, z), new Vector3f());
    }
}
