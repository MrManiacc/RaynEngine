package com.jgfx.utils;

import com.jgfx.blocks.Block;
import com.jgfx.engine.assets.model.Vao;

import java.util.Arrays;

/**
 * This is a helper class that will generate a vao for a chunk
 */
public class ShapeFactory {
    /**
     * @return returns a vao with the generated block. This won't be used for chunks
     */
    public static Vao generate(Block block) {
        var meshData = new MeshData();

        for (var element : block.getElements()) {
            for (var side : Side.values()) {
                if (element.hasSide(side)) {
                    var vertices = element.getVertices(side);
                    for (int i = 0; i < 4; i++) {
                        meshData.addVertex(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]);
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

        var vao = Vao.create(1);
        vao.bind();
        vao.createAttribute(meshData.getVertices(), 3);
        vao.createIndexBuffer(meshData.getIndices());
        vao.unbind();
        return vao;
    }
}
