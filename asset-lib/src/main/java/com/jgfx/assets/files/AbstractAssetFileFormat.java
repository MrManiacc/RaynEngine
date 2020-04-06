package com.jgfx.assets.files;

import com.jgfx.assets.data.AssetData;
import com.jgfx.assets.naming.Name;
import lombok.Getter;

/**
 * A base implementation of that will handle files with specified file extensions.
 * The name of the corresponding asset is assumed to be the non-extension part of the file name.
 *
 * @author Immortius
 */
public abstract class AbstractAssetFileFormat<T extends AssetData> implements AssetFileFormat<T> {
    @Getter
    private FileExtensionPathMatcher fileMatcher;

    /**
     * @param fileExtensions Additional file extensions that this file format will handle
     */
    public AbstractAssetFileFormat(String... fileExtensions) {
        this.fileMatcher = new FileExtensionPathMatcher(fileExtensions);
    }

    @Override
    public Name getAssetName(String filename) {
        int extensionStart = filename.lastIndexOf('.');
        if (extensionStart != -1) {
            return new Name(filename.substring(0, extensionStart));
        }
        return new Name(filename);
    }

}
