package com.jgfx.engine.ecs.group;


import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jgfx.engine.ecs.component.Component;
import com.jgfx.engine.ecs.entity.ref.EntityRef;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Creates a group of entities based on the given classes
 */
public class Group implements Iterable<EntityRef> {
    private final Iterable<EntityRef> parent;
    private final Map<Long, Iterable<EntityRef>> hashedIterables;

    Group(Iterable<EntityRef> iterable) {
        this.parent = iterable;
        this.hashedIterables = Maps.newConcurrentMap();
    }

    @Override
    public Iterator<EntityRef> iterator() {
        return parent.iterator();
    }

    /**
     * This method will walk the stack trace and create an iterator for the current hashed call
     */
    public final void forEach(Consumer<? super EntityRef> action, Predicate<EntityRef> predicate) {
        var stackTraceElements = Thread.currentThread().getStackTrace();
        var hash = hash(stackTraceElements);
        if (hashedIterables.containsKey(hash))
            hashedIterables.get(hash).forEach(action);
        else {
            var hashedIterable = Iterables.filter(parent, predicate::test);
            hashedIterables.put(hash, hashedIterable);
            hashedIterable.forEach(action);
        }
    }

    /**
     * This will do the beforeAction before each element, the process action on each element, then the post action on each element
     */
    public final void forEach(Consumer<? super EntityRef> beforeAction, Consumer<? super EntityRef> processAction, Consumer<? super EntityRef> postAction) {
        for (var element : this) {
            beforeAction.accept(element);
            processAction.accept(element);
            postAction.accept(element);
        }
    }


    /**
     * This method will walk the stack trace and create an iterator for the current hashed call and will do the runnable before everything
     */
    public final void forEach(Runnable before, Consumer<? super EntityRef> action, Predicate<EntityRef> predicate) {
        before.run();
        forEach(action, predicate);
    }

    /**
     * This method will walk the stack trace and create an iterator for the current hashed call and will do the runnable after everything
     */
    public final void forEach(Consumer<? super EntityRef> action, Predicate<EntityRef> predicate, Runnable after) {
        forEach(action, predicate);
        after.run();
    }

    /**
     * This method will walk the stack trace and create an iterator for the current hashed call and will do the runnable before and after everything
     */
    public final void forEach(Runnable before, Consumer<? super EntityRef> action, Predicate<EntityRef> predicate, Runnable after) {
        before.run();
        forEach(action, predicate);
        after.run();
    }

    /**
     * @return this will generate a has from the calling location
     */
    private long hash(StackTraceElement[] trace) {
        var hasher = trace[2];
        var line = hasher.getLineNumber();
        var className = hasher.getClassName();
        var methodName = hasher.getMethodName();
        return line + (long) className.hashCode() + (long) methodName.hashCode();
    }

    /**
     * Allows for pre and post of a group of elements
     */
    public void forEach(Runnable begin, Consumer<EntityRef> action, Runnable end) {
        begin.run();
        forEach(action);
        end.run();
    }

    /**
     * @return returns the number of entities for the group
     */
    public int count() {
        return Iterables.size(parent);
    }
}


