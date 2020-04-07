package com.jgfx.player.systems;

import com.badlogic.gdx.graphics.Camera;
import com.jgfx.engine.assets.config.Config;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.Resource;
import com.jgfx.engine.injection.anotations.Single;
import com.jgfx.engine.input.Input;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.window.IWindow;
import com.jgfx.player.data.PlayerCamera;
import com.jgfx.player.data.PlayerInfo;
import com.jgfx.player.data.PlayerTransform;
import org.joml.Vector3f;

/**
 * This class will move the player based upon the given input
 * TODO: replace this will some physics based movement
 */
@AutoRegister
public class SimplePlayerMove extends EntitySystem {
    @In private Input input;
    @In private IWindow window;
    @Single("engine:entities#local-player") private EntityRef localPlayer;
    @Resource("engine:config#player") private Config playerConfig;
    private float lastFocus = 0;

    /**
     * This will update the local player
     */
    @Override
    protected void process(EngineTime time) {
        var camera = localPlayer.getComponent(PlayerCamera.class);
        var transform = localPlayer.getComponent(PlayerTransform.class);
        var info = localPlayer.getComponent(PlayerInfo.class);
        processFocused(info);
        if (window.isFocused()) {
            processRotation(info, transform);
            processMovement(info, transform, camera);
            processCamera(transform, camera);
        }
    }

    /**
     * Updates the focused state based upon the focused key
     * This will wait 0.25 seconds before allowing a press again
     */
    private void processFocused(PlayerInfo info) {
        var now = time.getGameTime();
        var diff = now - lastFocus;
        if (input.keyPressed(info.toggleCameraKey) && diff >= 0.25f) {
            lastFocus = now;
            window.setFocused(!window.isFocused());
        }
    }

    /**
     * Processes the movement for the player, based on keyboard input
     */
    private void processMovement(PlayerInfo info, PlayerTransform transform, PlayerCamera camera) {
        var forward = camera.forward();
        var right = camera.right();
        var up = camera.up();
        var delta = time.getGameDelta();
        var multiplier = 1.0;
        if (input.keyDown(info.sprintKey))
            multiplier = info.sprintSpeed;
        if (input.keyDown(info.forwardKey)) {
            transform.x += (forward.x * delta * info.movementSpeed * multiplier);
            transform.y += (forward.y * delta * info.movementSpeed * multiplier);
            transform.z += (forward.z * delta * info.movementSpeed * multiplier);
        }
        if (input.keyDown(info.backwardKey)) {
            transform.x -= (forward.x * delta * info.movementSpeed * multiplier);
            transform.y -= (forward.y * delta * info.movementSpeed * multiplier);
            transform.z -= (forward.z * delta * info.movementSpeed * multiplier);
        }
        if (input.keyDown(info.rightKey)) {
            transform.x += (right.x * delta * info.strafeSpeed * multiplier);
            transform.y += (right.y * delta * info.strafeSpeed * multiplier);
            transform.z += (right.z * delta * info.strafeSpeed * multiplier);
        }
        if (input.keyDown(info.leftKey)) {
            transform.x -= (right.x * delta * info.strafeSpeed * multiplier);
            transform.y -= (right.y * delta * info.strafeSpeed * multiplier);
            transform.z -= (right.z * delta * info.strafeSpeed * multiplier);
        }
        if (input.keyDown(info.upKey)) {
//            transform.x += (up.x * delta * info.movementSpeed * multiplier);
            transform.y += (delta * info.movementSpeed * multiplier);
//            transform.z += (up.z * delta * info.movementSpeed * multiplier);
        }
        if (input.keyDown(info.downKey)) {
//            transform.x -= (up.x * delta * info.movementSpeed * multiplier);
            transform.y -= (delta * info.movementSpeed * multiplier);
//            transform.z -= (up.z * delta * info.movementSpeed * multiplier);
        }

    }

    /**
     * Processes the rotation for the player based on mouse input
     */
    private void processRotation(PlayerInfo info, PlayerTransform transform) {
        var delta = time.getGameDelta();
        transform.ry += (input.getDx() * delta * info.horizontalSensitivity);
        transform.rx += (input.getDy() * delta * info.verticalSensitivity);
    }

    /**
     * Updates the camera based upon the player's transform
     */
    private void processCamera(PlayerTransform transform, PlayerCamera camera) {
        camera.viewMatrix.identity()
                .rotateX((float) Math.toRadians(transform.rx))
                .rotateY((float) Math.toRadians(transform.ry))
                .translate(-transform.x, -transform.y, -transform.z)
                .scale(1.0f);
    }
}
