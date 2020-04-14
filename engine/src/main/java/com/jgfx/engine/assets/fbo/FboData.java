package com.jgfx.engine.assets.fbo;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.data.AssetData;
import com.jgfx.engine.window.IWindow;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class stores all of the parameters for a fbo. This will be created with a {@link FboBuilder}
 */
public class FboData implements AssetData {

    private final int width, height, sampleSize, attachmentType;
    private final boolean multisampled;
    public static final int DEPTH_BUFFER_ATTACHMENT = 0, DEPTH_TEXTURE_ATTACHMENT = 1;
    public static final FboData EMPTY = new FboData();

    private FboData() {
        this.width = -1;
        this.sampleSize = -1;
        this.attachmentType = -1;
        this.multisampled = false;
        this.height = -1;
    }

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
            this.width = (int) window.getFbWidth();
            this.height = (int) window.getFbHeight();
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (!(object instanceof FboData)) return false;

        FboData fboData = (FboData) object;

        return new EqualsBuilder()
                .append(width, fboData.width)
                .append(height, fboData.height)
                .append(sampleSize, fboData.sampleSize)
                .append(attachmentType, fboData.attachmentType)
                .append(multisampled, fboData.multisampled)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(width)
                .append(height)
                .append(sampleSize)
                .append(attachmentType)
                .append(multisampled)
                .toHashCode();
    }
}
