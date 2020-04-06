package com.jgfx.chunk.data;

import com.google.common.collect.Maps;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.entity.ref.EntityRef;
import com.jgfx.chunk.utils.ChunkDirection;

import java.util.Map;

import static com.jgfx.chunk.utils.ChunkHelper.CHUNK_BLOCK_SIZE;

/**
 * Stores all of the needed data about chunk neighbors
 */
public class ChunkNeighbors implements Component {
    private final Map<ChunkDirection, EntityRef> neighbors;
    private final EntityRef parent;

    public ChunkNeighbors(EntityRef parent) {
        this.neighbors = Maps.newConcurrentMap();
        this.parent = parent;
    }

    /**
     * Adds a neighbor, will return true if the chunk is in fact a neighbor
     *
     * @param chunk the chunk to add
     * @return returns true if added
     */
    public boolean addNeighbor(EntityRef chunk) {
        var direction = getDirection(chunk);
        if (direction == ChunkDirection.INVALID) return false;
        return addNeighbor(direction, chunk);
    }

    /**
     * Adds a neighbor without computing the direction
     *
     * @param chunkDirection the direction to add for
     * @param chunk     the chunk to add
     * @return returns true if added
     */
    public boolean addNeighbor(ChunkDirection chunkDirection, EntityRef chunk) {
        if (hasNeighbor(chunkDirection))
            return false;
        neighbors.put(chunkDirection, chunk);
        var neighbors = chunk.getComponent(ChunkNeighbors.class);
        neighbors.addNeighbor(parent); //The inverse, this current chunk to the neighbor chunk as a neighbor
        return true;
    }

    /**
     * Returns true if all of the neighbors are present
     *
     * @return true if neighbors present
     */
    public boolean hasAllNeighbors() {
        for (var dir : ChunkDirection.values()) {
            if (dir != ChunkDirection.INVALID)
                if (!hasNeighbor(dir)) return false;
        }
        return true;
    }

    /**
     * This will remove a neighbor by it's direction
     *
     * @param chunkDirection the direction of the neighbor
     * @return returns true if the neighbor was removed
     */
    public boolean removeNeighbor(ChunkDirection chunkDirection) {
        if (!neighbors.containsKey(chunkDirection)) return false;
        var removed = neighbors.remove(chunkDirection);
        var neighbors = removed.getComponent(ChunkNeighbors.class);
        neighbors.removeNeighbor(origin());//The inverse, if the chunk isn't being deleted, we need to notify it that this chunk has removed it as a neighbor
        return true;
    }

    /**
     * @return returns the parent chunks origin
     */
    private ChunkOrigin origin() {
        return parent.getComponent(ChunkOrigin.class);
    }

    /**
     * This will remove the neighbor by the chunk reference
     *
     * @param chunk the chunk to remove
     * @return returns true if the neighbor was removed
     */
    public boolean removeNeighbor(EntityRef chunk) {
        var direction = getDirection(chunk);
        if (direction == ChunkDirection.INVALID)
            return false;
        return removeNeighbor(direction);
    }

    /**
     * This will remove the neighbor by the chunk reference
     *
     * @param chunk the chunk to remove
     * @return returns true if the neighbor was removed
     */
    public boolean removeNeighbor(ChunkOrigin chunk) {
        var direction = getDirection(chunk);
        if (direction == ChunkDirection.INVALID)
            return false;
        return removeNeighbor(direction);
    }

    /**
     * Checks to see if the neighbor is present
     *
     * @param chunkDirection the direction of the neighbor
     * @return returns true if the neighbor is present
     */
    public boolean hasNeighbor(ChunkDirection chunkDirection) {
        return neighbors.containsKey(chunkDirection);
    }


    /**
     * Checks to see if the given chunk is a neighbor to this chunk
     *
     * @param other the chunk to check
     * @return returns true if the chunk is a neighbor of this chunk
     */
    public boolean isNeighbor(EntityRef other) {
        return getDirection(other) != ChunkDirection.INVALID;
    }


    /**
     * Gets the direction of the other chunk, or invalid if not a neighbor
     *
     * @param other the chunk to get the direction for
     * @return returns the direction or invalid
     */
    public ChunkDirection getDirection(ChunkOrigin j) {
        var i = this.origin();
        if (i.x == j.x && i.y == j.y && i.z == j.z)
            return ChunkDirection.INVALID;
        if (i.y == j.y && i.z == j.z) {
            if (i.x + CHUNK_BLOCK_SIZE == j.x)
                return ChunkDirection.EAST;
            else if (i.x - CHUNK_BLOCK_SIZE == j.x)
                return ChunkDirection.WEST;
        } else if (i.y == j.y && i.x == j.x) {
            if (i.z + CHUNK_BLOCK_SIZE == j.z)
                return ChunkDirection.NORTH;
            else if (i.z - CHUNK_BLOCK_SIZE == j.z)
                return ChunkDirection.SOUTH;
        } else if (i.x == j.x && i.z == j.z)
            if (i.y + CHUNK_BLOCK_SIZE == j.y)
                return ChunkDirection.UP;
            else if (i.y - CHUNK_BLOCK_SIZE == j.y)
                return ChunkDirection.DOWN;
        return ChunkDirection.INVALID;
    }

    /**
     * Gets the direction of the other chunk, or invalid if not a neighbor
     *
     * @param other the chunk to get the direction for
     * @return returns the direction or invalid
     */
    public ChunkDirection getDirection(EntityRef other) {
        return getDirection(other.getComponent(ChunkOrigin.class));
    }


    /**
     * Get's a neighbor in the given direction, or null if not present
     *
     * @param chunkDirection the direction of the neighbor
     * @return returns the neighbor or null
     */
    public EntityRef neighbor(ChunkDirection chunkDirection) {
        return neighbors.get(chunkDirection);
    }


    @Override
    public String toString() {
        var builder = new StringBuilder();
        var added = false;
        for (var neighbor : neighbors.keySet()) {
            builder.append(neighbor.name().toLowerCase());
            builder.append(", ");
            added = true;
        }
        var output = builder.toString();
        if (added)
            output = output.substring(0, output.length() - 2);
        return "Neighbors: [" + output + "]";
    }
}
