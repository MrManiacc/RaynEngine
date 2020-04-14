package com.jgfx.engine.assets.fbo;

import com.jgfx.assets.Asset;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.type.AssetType;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.window.IWindow;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

/**
 * Represents a frame buffer object. The goal of this asset is to allow for binding of a separate render target,
 * then rendering to it. Next you'll bind back to the main shader and you can use the fbo as a texture, and render it to
 * whatever we like. We can also store depth texture data, for more manipulation.
 */
public class Fbo extends Asset<FboData> {
    private static final Logger logger = LogManager.getLogger(Fbo.class);
    private int colorTexture, depthTexture, fboId, depthBuffer, colorBuffer;
    @Getter private int width, height;
    private IWindow window;

    /**
     * The constructor for an asset. It is suggested that implementing classes provide a constructor taking both the urn, and an initial AssetData to load.
     *
     * @param urn       The urn identifying the asset.
     * @param assetType The asset type this asset belongs to.
     */
    public Fbo(ResourceUrn urn, AssetType<?, FboData> assetType, FboData data) {
        super(urn, assetType);
        reload(data);
    }

    @Override
    public void reload(FboData data) {
        this.window = CoreContext.get(IWindow.class);
        this.width = data.getWidth();
        this.height = data.getHeight();
        createFrameBuffer();
        if (data.isMultisampled())
            createMultisampledColorBuffer(data.getSampleSize(), data.getWidth(), data.getHeight());
        else
            createTextureAttachment(data.getWidth(), data.getHeight());
        if (data.getAttachmentType() == FboData.DEPTH_BUFFER_ATTACHMENT)
            createDepthBufferAttachment(data.isMultisampled(), data.getSampleSize(), data.getWidth(), data.getHeight());
        else if (data.getAttachmentType() == FboData.DEPTH_TEXTURE_ATTACHMENT)
            createDepthTextureAttachment(data.getWidth(), data.getHeight());
        unbind();
        logger.info("Reloaded fbo, {}, successfully ", getUrn().toString());
    }

    /**
     * Creates the fbo
     */
    private void createFrameBuffer() {
        fboId = glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
    }

    /**
     * Creates and binds the texture to the frame buffer texture type
     */
    private void createTextureAttachment(int width, int height) {
        colorTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, colorTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture, 0);
    }

    /**
     * Creates and binds the depth texture to the frame buffer texture type
     */
    private void createDepthTextureAttachment(int width, int height) {
        depthTexture = GL11.glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
    }

    /**
     * Binds the texture for rendering
     */
    public void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, colorTexture);
    }

    /**
     * Binds the texture for rendering
     */
    public void bindDepthTexture() {
        glBindTexture(GL_TEXTURE_2D, depthTexture);
    }

    /**
     * Unbinds the texture
     */
    public void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Creates a multi sampled color buffer with the given sample size, and width/height
     */
    private void createMultisampledColorBuffer(int sampleSize, int width, int height) {
        colorBuffer = GL30.glGenRenderbuffers();
        glBindRenderbuffer(GL30.GL_RENDERBUFFER, colorBuffer);
        glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, sampleSize, GL11.GL_RGB8, width, height);
        glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, colorBuffer);
    }

    /**
     * Creates a optionally multi sampled depth buffer
     */
    private void createDepthBufferAttachment(boolean multisampled, int sampleSize, int width, int height) {
        depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        if (!multisampled) {
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
        } else {
            GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, sampleSize, GL14.GL_DEPTH_COMPONENT24, width, height);
        }
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
    }

    /**
     * Unbinds the fbo and sets back to main render buffer
     */
    public void unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }


    /**
     * Deletes the fbo
     */
    public void destroy() {
        glDeleteFramebuffers(fboId);
        glDeleteTextures(colorTexture);
        glDeleteTextures(depthTexture);
        glDeleteRenderbuffers(depthBuffer);
        glDeleteRenderbuffers(colorBuffer);
    }

    /**
     * @param textureLocation represents the location of the texture to write to
     *                        Resolves to the given fbo
     */
    public void resolveToFbo(int textureLocation, Fbo output) {
        var texture = GL_COLOR_ATTACHMENT0 + textureLocation;
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, output.fboId);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.fboId);
        GL11.glReadBuffer(texture);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, output.width, output.height, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        unbind();
    }

    /**
     * Resolves to the other fbo with the GL_COLOR_ATTACHMENT0 by just passing 0
     */
    public void resolveToFbo(Fbo output) {
        resolveToFbo(0, output);
    }

    /**
     * Binds the fbo to be drawn to
     */
    public void bindToDraw() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fboId);
        GL11.glViewport(0, 0, width, height);
    }

    /**
     * Resolves to the main display
     */
    public void resolveToScreen(int textureLocation) {
        var texture = GL_COLOR_ATTACHMENT0 + textureLocation;
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fboId);
        GL11.glDrawBuffer(GL11.GL_BACK);
        GL11.glReadBuffer(texture);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, (int) window.getFbWidth(), (int) window.getFbHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        unbind();
    }

    /**
     * Resolves to main display as deafult attachment
     */
    public void resolveToScreen() {
        resolveToScreen(0);
    }

}
