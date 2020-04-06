package com.jgfx.blocks.data;

import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.utils.Side;
import org.joml.Vector4i;

import java.text.DecimalFormat;

/**
 * Represents a block face
 */
public class BlockFace {
    private Side direction;
    private Vector4i uv;
    private ResourceUrn textureUrn;

    public BlockFace(Side direction, Vector4i uv, ResourceUrn textureUrn) {
        this.direction = direction;
        this.uv = uv;
        this.textureUrn = textureUrn;
    }

    public Side getDirection() {
        return direction;
    }

    public Vector4i getUv() {
        return uv;
    }

    public ResourceUrn getTextureUrn() {
        return textureUrn;
    }

    @Override
    public String toString() {
        return "BlockFace{" +
                "direction=" + direction.name().toLowerCase() +
                ", uv=" + uv.toString(DecimalFormat.getInstance()) +
                ", textureUrn=" + textureUrn +
                '}';
    }
}
