package com.jgfx.blocks;

import com.google.common.collect.Lists;
import com.jgfx.assets.Asset;
import com.jgfx.assets.type.AssetType;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.blocks.data.BlockData;
import com.jgfx.blocks.data.BlockElement;
import com.jgfx.engine.assets.Assets;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.tiles.Tile;
import com.jgfx.tiles.atlas.Atlas;
import com.jgfx.utils.MeshData;
import com.jgfx.utils.Side;
import lombok.Getter;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Represents a generic block that can be loaded from file
 */
public class Block extends Asset<BlockData> {
    @Getter private List<BlockElement> elements = Lists.newArrayList();
    @Getter private long id;
    @Getter private String displayName;

    /**
     * The constructor for an asset. It is suggested that implementing classes provide a constructor taking both the urn, and an initial AssetData to load.
     *
     * @param urn       The urn identifying the asset.
     * @param assetType The asset type this asset belongs to.
     */
    public Block(ResourceUrn urn, AssetType<?, BlockData> assetType, BlockData data) {
        super(urn, assetType);
        reload(data);
    }

    /**
     * Loads the block from the data loaded
     *
     * @param data The data to load.
     */
    @Override
    public void reload(BlockData data) {
        elements.clear();
        elements.addAll(data.getElements());
        id = data.getId();
        displayName = data.getDisplayName();
        Blocks.addBlock(this);
    }

    /**
     * This will add the block to the static block data
     */
    public void addToChunk(int x, int y, int z, byte neighborMeta, Atlas atlas, MeshData meshData) {
        var sides = Side.getSides(neighborMeta);
        for (var element : elements)
            for (var side : sides) {
                if (element.hasSide(side)) {
                    var tile = Assets.get(element.getTile(side), Tile.class).get();
                    var tileData = tile.getCoords();
                    var uv = element.getUv(side);
                    System.out.println(uv.toString(DecimalFormat.getInstance()));
                    var vertices = element.getVertices(side);
                    var normal = element.getNormal(side);
                    for (int i = 0; i < 4; i++) {
                        if (i == 0)//top left
                            meshData.addUv(tileData.x, tileData.y);
                        if (i == 1)//top right
                            meshData.addUv(tileData.z, tileData.y);
                        if (i == 2)//bottom left
                            meshData.addUv(tileData.x, tileData.w);
                        if (i == 3)//bottom right
                            meshData.addUv(tileData.z, tileData.w);
                        meshData.addNormal(normal.x, normal.y, normal.z);
                        meshData.addVertex(vertices[i * 3] + x, vertices[i * 3 + 1] + y, vertices[i * 3 + 2] + z);
                    }
                    int length = meshData.getVertexCount();
                    meshData.addIndices(length - 4);
                    meshData.addIndices(length - 3);
                    meshData.addIndices(length - 2);

                    meshData.addIndices(length - 2);
                    meshData.addIndices(length - 3);
                    meshData.addIndices(length - 1);
                }
            }
    }

}
