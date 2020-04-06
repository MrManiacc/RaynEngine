package com.jgfx.chunk.utils;

import org.joml.Vector3i;

/**
 * This class does simple math regarding chunks
 */
public class ChunkHelper {
    public static final int CHUNK_BLOCK_SIZE = 32;
    public static final int CHUNK_SIZE = 2;
    public static final int CHUNK_SIZE_CUBED = 32768;
    public static final float BLOCK_SIZE = 0.5f;
    //we allow for 32 view distance
    public static final int VIEW_DISTANCE = 32;
    public static final int MAX_CHUNKS = 32 * 32 * 32;

    /**
     * @return returns the index of the block for the position
     */
    public static long getIndex(int x, int y, int z) {
        return (z * CHUNK_BLOCK_SIZE * CHUNK_BLOCK_SIZE) + (y * CHUNK_BLOCK_SIZE) + x;
    }

    /**
     * @return returns the index of the block for the position
     */
    public static long getIndex(Vector3i position) {
        return getIndex(position.x, position.y, position.z);
    }

    /**
     * @return returns the index of the block for the position
     */
    public static long getChunkIndex(int x, int y, int z) {
        return (z * CHUNK_SIZE * CHUNK_SIZE) + (y * CHUNK_SIZE) + x;
    }

    /**
     * @return returns the index of the block for the position
     */
    public static long getChunkIndex(Vector3i position) {
        return getChunkIndex(position.x, position.y, position.z);
    }


    /**
     * @return returns the position from the given index as a {@link org.joml.Vector3f}.
     */
    public static Vector3i getPositionVec(int index) {
        var pos = getPosition(index);
        return new Vector3i(pos[0], pos[1], pos[2]);
    }

    /**
     * @return returns the position from the given index
     */
    public static int[] getPosition(int index) {
        final int z = index / (CHUNK_BLOCK_SIZE * CHUNK_BLOCK_SIZE);
        index -= (z * CHUNK_BLOCK_SIZE * CHUNK_BLOCK_SIZE);
        final int y = index / CHUNK_BLOCK_SIZE;
        final int x = index % CHUNK_BLOCK_SIZE;
        return new int[]{x, y, z};
    }


}
