package com.jgfx.utils;

import org.joml.Vector3i;

import java.util.Arrays;

public enum Side {
    NORTH(new Vector3i(0, 0, 1), 0), SOUTH(new Vector3i(0, 0, -1), 1), EAST(new Vector3i(1, 0, 0), 2), WEST(new Vector3i(-1, 0, 0), 3), UP(new Vector3i(0, 1, 0), 4), DOWN(new Vector3i(0, -1, 0), 5);
    public Vector3i normal;
    private int sideID;

    Side(Vector3i normal, int sideID) {
        this.normal = normal;
        this.sideID = sideID;
    }

    /**
     * @return returns true if the side is contained in the neighborMeta
     */
    public boolean isSide(byte neighborMeta) {
        return ((neighborMeta >> sideID) & 1) == 1;
    }

    /**
     * @return returns the input byte plus the supplied side
     */
    public byte addSide(byte neighborMeta) {
        return (byte) (neighborMeta | (1 << sideID));
    }

    /**
     * @return returns the byte without the supplied side
     */
    public byte removeSide(byte neighborMeta) {
        return (byte) (neighborMeta & (1 << sideID));
    }

    /**
     * @return returns all of the sides with the neighbor meta
     */
    public static Side[] getSides(byte neighborMeta) {
        return Arrays.stream(Side.values())
                .filter(s -> s.isSide(neighborMeta))
                .toArray(Side[]::new);
    }
}
