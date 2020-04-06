package com.jgfx.assets.files;

import com.jgfx.assets.naming.Name;

import java.nio.file.PathMatcher;

/**
 * Common base interface for all file formats.  A file format is used to load one or more files and either create or modify an
 *
 * @author Immortius
 */
public interface FileFormat {
    /**
     * @return A path matcher that will filter for files relevant for this format.
     */
    PathMatcher getFileMatcher();

    /**
     * This method is use to obtain the name of the resource represented by the given filename. The ModuleAssetDataProducer will combine it with a module id to
     * determine the complete ResourceUrn.
     *
     * @param filename The filename of an asset, including extension
     * @return The asset name corresponding to the given filename
     */
    Name getAssetName(String filename);
}
