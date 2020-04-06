package com.jgfx.utils;

import com.jgfx.chunk.utils.Groups;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.ecs.group.GroupBuilder;

import java.util.Arrays;

/**
 * Adds support for our group
 */
public class GroupsBuilder extends GroupBuilder {
    /**
     * @return returns the current group, which allows us to add other Excludes, Alls, etc.
     */
    public GroupBuilder group(Groups group) {
        allTypes.addAll(Arrays.asList(group.value));
        return this;
    }
}
