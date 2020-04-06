package com.jgfx.blocks.data;

import com.google.common.collect.Maps;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.utils.Side;
import lombok.Getter;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4i;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents an element of a block
 */
public class BlockElement {
    @Getter
    private final Vector3i from, to;

    private final Map<Side, BlockFace> blockFaceMap = Maps.newConcurrentMap();
    private final Map<Side, float[]> blockVerticesMap = Maps.newConcurrentMap();
    private static final float SIZE = 32.0f;

    public BlockElement(Vector3i from, Vector3i to, BlockFace... faces) {
        this.from = from;
        this.to = to;
        for (var face : faces)
            addFace(face);
    }

    /**
     * Adds a face to the element
     */
    public void addFace(BlockFace face) {
        blockFaceMap.put(face.getDirection(), face);
        addVertices(face.getDirection());
    }

    /**
     * Adds the vertices for the given face
     */
    private void addVertices(Side side) {
        var min = new Vector3f((from.x / SIZE) - 0.5f, (from.y / SIZE) - 0.5f, (from.z / SIZE) - 0.5f);
        var max = new Vector3f((to.x / SIZE) - 0.5f, (to.y / SIZE) - 0.5f, (to.z / SIZE) - 0.5f);
        switch (side) {
            case UP:
                blockVerticesMap.put(Side.UP, new float[]{
                        min.x, max.y, min.z,
                        min.x, max.y, max.z,

                        max.x, max.y, min.z,
                        max.x, max.y, max.z
                });
                break;
            case DOWN:
                blockVerticesMap.put(Side.DOWN, new float[]{
                        min.x, min.y, max.z,
                        min.x, min.y, min.z,

                        max.x, min.y, max.z,
                        max.x, min.y, min.z
                });
                break;
            case EAST:
                blockVerticesMap.put(Side.EAST, new float[]{
                        max.x, max.y, max.z,
                        max.x, min.y, max.z,

                        max.x, max.y, min.z,
                        max.x, min.y, min.z
                });
                break;
            case WEST:
                blockVerticesMap.put(Side.WEST, new float[]{
                        min.x, max.y, min.z,
                        min.x, min.y, min.z,

                        min.x, max.y, max.z,
                        min.x, min.y, max.z
                });
                break;
            case NORTH:
                blockVerticesMap.put(Side.NORTH, new float[]{
                        min.x, max.y, max.z,
                        min.x, min.y, max.z,

                        max.x, max.y, max.z,
                        max.x, min.y, max.z
                });
                break;
            case SOUTH:
                blockVerticesMap.put(Side.SOUTH, new float[]{
                        max.x, max.y, min.z,
                        max.x, min.y, min.z,

                        min.x, max.y, min.z,
                        min.x, min.y, min.z
                });
                break;
        }
    }


    /**
     * @return returns uv for the specified direction or null if not face is present
     */
    public Vector4i getUv(Side direction) {
        return blockFaceMap.get(direction).getUv();
    }

    /**
     * @return returns the normal for the direction, this is a helper
     */
    public Vector3i getNormal(Side direction) {
        return direction.normal;
    }

    /**
     * @return gets the texture for the given direction
     */
    public ResourceUrn getTile(Side direction) {
        return blockFaceMap.get(direction).getTextureUrn();
    }

    /**
     * @return returns the vertices for the given side
     */
    public float[] getVertices(Side side) {
        return blockVerticesMap.get(side);
    }

    /**
     * @return returns all of the vertices for the block face
     */
    public float[] getVertices() {
        var vertices = new ArrayList<Float>();
        blockVerticesMap.values().forEach(verts -> {
            for (var vert : verts)
                vertices.add(vert);
        });
        var outVertices = new float[vertices.size()];
        for (var i = 0; i < vertices.size(); i++)
            outVertices[i] = vertices.get(i);
        return outVertices;
    }

    /**
     * @return returns true if the side is present
     */
    public boolean hasSide(Side side) {
        return blockFaceMap.containsKey(side);
    }

    @Override
    public String toString() {
        String result = blockFaceMap.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "{", "}"));
        return "BlockElement{" +
                "from=" + from.toString(DecimalFormat.getInstance()) +
                ", to=" + to.toString(DecimalFormat.getInstance()) +
                ", blockFaceMap=" + result +
                '}';
    }

}
