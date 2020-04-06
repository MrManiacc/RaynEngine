package com.jgfx.assets.context;

import com.google.common.collect.Maps;
import com.jgfx.assets.urn.ResourceUrn;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

public class Context implements IContext {
    @Getter
    private final Optional<IContext> parent;
    private final Map<Class<?>, Object> map = Maps.newConcurrentMap();
    private final Map<Class<?>, Map<ResourceUrn, Object>> namedMap = Maps.newConcurrentMap();

    public Context(IContext parent) {
        this.parent = Optional.of(parent);
    }

    public Context() {
        this.parent = Optional.empty();
    }

    @Override
    public <T> T get(Class<? extends T> type) {
        if (type == Context.class) {
            return type.cast(this);
        }
        T result = type.cast(map.get(type));
        if (result != null)
            return result;
        return parent.<T>map(iContext -> iContext.get(type)).orElse(null);
    }

    @Override
    public <T> T get(ResourceUrn resourceUrn, Class<? extends T> type) {
        if (namedMap.containsKey(type)) {
            var map = namedMap.get(type);
            if (map.containsKey(resourceUrn))
                return type.cast(map.get(resourceUrn));
        }
        return parent.<T>map(iContext -> iContext.get(resourceUrn, type)).orElse(null);
    }

    @Override
    public void put(Object object) {
        map.put(object.getClass(), object);
    }


    @Override
    public void put(Class<?> clazz, Object object) {
        map.put(clazz, object);
    }

    @Override
    public void put(ResourceUrn resourceUrn, Object object) {
        var type = object.getClass();
        var map = namedMap.computeIfAbsent(type, k -> Maps.newConcurrentMap());
        map.put(resourceUrn, object);
    }
}
