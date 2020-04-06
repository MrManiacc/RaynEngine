package com.jgfx.blocks.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jgfx.assets.data.AssetData;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.injection.anotations.Resource;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores data about a block, that will be loaded into the actual block. This is like a buffer kinda.
 */
public class BlockData implements AssetData {
    @Getter
    private final Optional<ResourceUrn> parent;
    @Getter
    private int id;
    @Getter
    private String displayName;

    private List<BlockElement> blockElements = Lists.newArrayList();

    public BlockData(int id, String displayName) {
        this(id, displayName, Optional.empty());
    }

    public BlockData(int id, String displayName, Optional<ResourceUrn> parent) {
        this.id = id;
        this.displayName = displayName;
        this.parent = parent;
    }

    /**
     * Adds a new block element
     */
    public void addElement(BlockElement element) {
        blockElements.add(element);
    }

    /**
     * @return returns an iterator for going through all of the block elements
     */
    public Collection<BlockElement> getElements() {
        return blockElements;
    }


    @Override
    public String toString() {
        String result = blockElements.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("-", "{", "}"));
        return "BlockData{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", blockElements='" + result + '\'' +
                '}';
    }
}
