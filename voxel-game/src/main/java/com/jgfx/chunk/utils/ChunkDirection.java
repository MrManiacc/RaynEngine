package com.jgfx.chunk.utils;

/**
 * A generic direction represented by an enum
 */
public enum ChunkDirection {
    NORTH, SOUTH, EAST, WEST, UP, DOWN, INVALID;

    /**
     * Gets the opposite direction
     *
     * @param input the input dir
     * @return the opposite direction
     */
    public static ChunkDirection getOpposite(ChunkDirection input) {
        if (input == NORTH)
            return SOUTH;
        if (input == SOUTH)
            return NORTH;
        if (input == EAST)
            return WEST;
        if (input == WEST)
            return EAST;
        if (input == UP)
            return DOWN;
        if (input == DOWN)
            return UP;
        return INVALID;
    }
}
