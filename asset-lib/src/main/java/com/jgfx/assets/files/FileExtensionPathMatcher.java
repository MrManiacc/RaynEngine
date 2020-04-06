package com.jgfx.assets.files;


import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * A PathMatcher that matches files ending with one of a set of file extensions.
 *
 * @author Immortius
 */
public class FileExtensionPathMatcher implements PathMatcher {

    private final Set<String> extensions;

    /**
     * @param extensions Additional extensions that files must have to match
     */
    public FileExtensionPathMatcher(String... extensions) {
        this(Arrays.asList(extensions));

    }

    /**
     * @param extensions The extensions that files must have to match. Must not be empty
     */
    public FileExtensionPathMatcher(Collection<String> extensions) {
        Preconditions.checkNotNull(extensions);
        Preconditions.checkArgument(!extensions.isEmpty(), "At least one extension must be provided");
        this.extensions = Sets.newHashSet(extensions);
    }

    @Override
    public boolean matches(Path path) {
        Path fileName = path.getFileName();
        if (fileName != null) {
            return extensions.contains(Files.getFileExtension(fileName.toString()));
        }
        return false;
    }
}
