package com.jgfx.gui.fbo;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.data.AssetData;
import com.jgfx.engine.window.IWindow;
import lombok.Getter;

/**
 * This class stores all of the parameters for a fbo. This will be created with a {@link FboBuilder}
 */
public class FboData implements AssetData {

    private final int width, height, sampleSize, attachmentType;
    private final boolean multisampled;
    public static final int DEPTH_BUFFER_ATTACHMENT = 0, DEPTH_TEXTURE_ATTACHMENT = 1;

    public FboData(int attachmentType, int width, int height, boolean multisampled, int sampleSize) {
        this.attachmentType = attachmentType;
        this.width = width;
        this.height = height;
        this.multisampled = multisampled;
        this.sampleSize = sampleSize;
    }

    public FboData(int attachmentType, boolean multisampled, int sampleSize) {
        this.attachmentType = attachmentType;
        var window = CoreContext.get(IWindow.class);
        if (window != null) {
            this.width = (int) window.getWidth();
            this.height = (int) window.getHeight();
        } else {
            this.width = 1080;
            this.height = 720;
        }
        this.multisampled = multisampled;
        this.sampleSize = sampleSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public int getAttachmentType() {
        return attachmentType;
    }

    public boolean isMultisampled() {
        return multisampled;
    }
}
