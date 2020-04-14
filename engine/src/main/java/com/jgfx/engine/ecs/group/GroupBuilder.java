package com.jgfx.engine.ecs.group;

import com.google.common.collect.Sets;
import com.jgfx.assets.context.CoreContext;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.entity.pool.EntityManager;

import java.util.Arrays;
import java.util.Set;

/**
 * Helps to build entities subscriptions
 */
public class GroupBuilder {

    protected final Set<Class<? extends Component>> allTypes, oneTypes, excludeTypes;
    protected Class<? extends Component>[] allClasses, oneClasses, excludeClasses;

    public GroupBuilder() {
        allTypes = Sets.newHashSet();
        oneTypes = Sets.newHashSet();
        excludeTypes = Sets.newHashSet();
    }

    public GroupBuilder all(Class<? extends Component>... classes) {
        allTypes.addAll(Arrays.asList(classes));
        return this;
    }

    public GroupBuilder one(Class<? extends Component>... classes) {
        oneTypes.addAll(Arrays.asList(classes));
        return this;
    }

    public GroupBuilder exclude(Class<? extends Component>... classes) {
        excludeTypes.addAll(Arrays.asList(classes));
        return this;
    }

    /**
     * Checks to see if this is an empty builder
     */
    public boolean isEmpty() {
        return allTypes.isEmpty() && oneTypes.isEmpty();
    }

    private void extractClasses() {
        allClasses = new Class[allTypes.size()];
        var i = 0;
        for (var all : allTypes)
            allClasses[i++] = all;
        i = 0;
        oneClasses = new Class[oneTypes.size()];
        for (var one : oneTypes)
            oneClasses[i++] = one;
        i = 0;
        excludeClasses = new Class[excludeTypes.size()];
        for (var one : excludeTypes)
            excludeClasses[i++] = one;
    }

    /**
     * Builds the group using the manager, and the supplied all/one classes
     *
     * @param manager
     * @return returns the mapped iterator
     */
    public Group build(/*EntityManager manager*/) {
        var manager = CoreContext.get(EntityManager.class);
        extractClasses();
        if (!allTypes.isEmpty() && oneTypes.isEmpty() && excludeTypes.isEmpty())
            return new Group(manager.getEntitiesWithAll(allClasses));
        else if (allTypes.isEmpty() && !oneTypes.isEmpty() && excludeTypes.isEmpty())
            return new Group(manager.getEntitiesWithOne(oneClasses));
        else if (!allTypes.isEmpty() && !excludeTypes.isEmpty() && oneTypes.isEmpty())
            return new Group(manager.getEntitiesWithAllAndExclude(allClasses, excludeClasses));
        else if (allTypes.isEmpty() && !oneTypes.isEmpty())
            return new Group(manager.getEntitiesWithOneAndExclude(oneClasses, excludeClasses));
        else if (!allTypes.isEmpty() && excludeTypes.isEmpty())
            return new Group(manager.getEntitiesWithOneOrAll(oneClasses, allClasses));
        else if (!allTypes.isEmpty()) {
            return new Group(manager.getEntitiesWithOneOrAllAndExclude(oneClasses, allClasses, excludeClasses));
        } else
            return new Group(manager.getAllEntities());
    }
}
