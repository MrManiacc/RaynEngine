package com.jgfx.player.data;

import com.jgfx.engine.ecs.component.Component;

/**
 * Stores the loaded input values from the player config
 */
public class PlayerInfo implements Component {
    public final int forwardKey, backwardKey, leftKey, rightKey, upKey, downKey, sprintKey, toggleCameraKey;
    public final double verticalSensitivity, horizontalSensitivity, movementSpeed, strafeSpeed, sprintSpeed;

    public PlayerInfo(int forwardKey, int backwardKey, int leftKey, int rightKey, int upKey, int downKey, int sprintKey, int toggleCameraKey, double verticalSensitivity, double horizontalSensitivity, double movementSpeed, double strafeSpeed, double sprintSpeed) {
        this.forwardKey = forwardKey;
        this.backwardKey = backwardKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.downKey = downKey;
        this.sprintKey = sprintKey;
        this.toggleCameraKey = toggleCameraKey;
        this.verticalSensitivity = verticalSensitivity;
        this.horizontalSensitivity = horizontalSensitivity;
        this.movementSpeed = movementSpeed;
        this.strafeSpeed = strafeSpeed;
        this.sprintSpeed = sprintSpeed;
    }
}
