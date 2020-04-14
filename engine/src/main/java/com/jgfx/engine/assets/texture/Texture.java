package com.jgfx.engine.assets.texture;

import com.jgfx.assets.Asset;
import com.jgfx.assets.type.AssetType;
import com.jgfx.assets.urn.ResourceUrn;
import lombok.Getter;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

public class Texture extends Asset<TextureData> {
    private volatile int id;
    private int target;
    @Getter private float width, height;

    /**
     * The constructor for an asset. It is suggested that implementing classes provide a constructor taking both the urn, and an initial AssetData to load.
     *
     * @param urn       The urn identifying the asset.
     * @param assetType The asset type this asset belongs to.
     */
    public Texture(ResourceUrn urn, AssetType<?, TextureData> assetType, TextureData data) {
        super(urn, assetType);
        reload(data);
    }

    /**
     * Binds the texture with the given type
     */
    public void bind() {
        glBindTexture(target, id);
    }

    /**
     * Unbind the texture
     */
    public void unbind() {
        glBindTexture(target, 0);
    }


    /**
     * Reload the texture asset
     *
     * @param data The data to load.
     */
    public void reload(TextureData data) {
        //Load the texture
        this.width = data.getWidth();
        this.height = data.getHeight();
        this.id = glGenTextures();
        this.target = data.getTarget();
        bind();
        for (var name : data.getTextureParameters().keySet())
            for (var value : data.getTextureParameters().get(name))
                glTexParameteri(target, name, value);
        glTexImage2D(GL_TEXTURE_2D, 0, data.getInternalFormat(), data.getWidth(), data.getHeight(), 0, data.getFormat(), GL_UNSIGNED_BYTE, data.getData());
        unbind();
    }
}
