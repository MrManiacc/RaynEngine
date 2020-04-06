package com.jgfx.engine.assets.config;

import com.google.common.collect.Maps;
import com.jgfx.assets.Asset;
import com.jgfx.assets.type.AssetType;
import com.jgfx.assets.urn.ResourceUrn;

import java.util.Map;

public class Config extends Asset<ConfigData> {
    private ConfigData data;
    private Map<String, Integer> cachedInts = Maps.newConcurrentMap();
    private Map<String, Boolean> cachedBooleans = Maps.newConcurrentMap();
    private Map<String, String> cachedStrings = Maps.newConcurrentMap();
    private Map<String, Double> cachedDoubles = Maps.newConcurrentMap();

    /**
     * The constructor for an asset. It is suggested that implementing classes provide a constructor taking both the urn, and an initial AssetData to load.
     *
     * @param urn       The urn identifying the asset.
     * @param assetType The asset type this asset belongs to.
     */
    public Config(ResourceUrn urn, AssetType<?, ConfigData> assetType, ConfigData data) {
        super(urn, assetType);
        reload(data);
    }

    @Override
    public void reload(ConfigData data) {
        resetCache();
        this.data = data;
    }

    /**
     * @return returns an int inside the group, and with the name
     */
    public int getInt(String group, String name) {
        if (cachedInts.containsKey(group + "_" + name))
            return cachedInts.get(group + "_" + name);
        for (var wini : data.getConfigs()) {
            var value = wini.get(group, name, int.class);
            if (value != null) {
                cachedInts.put(group + "_" + name, value);
                return value;
            }
        }
        return -1;
    }

    /**
     * @return returns an int inside the group, and with the name
     */
    public String getString(String group, String name) {
        if (cachedStrings.containsKey(group + "_" + name))
            return cachedStrings.get(group + "_" + name);
        for (var wini : data.getConfigs()) {
            var value = wini.get(group, name);
            if (value != null) {
                cachedStrings.put(group + "_" + name, value);
                return value;
            }
        }
        return null;
    }

    /**
     * @return returns an int inside the group, and with the name
     */
    public boolean getBoolean(String group, String name) {
        if (cachedBooleans.containsKey(group + "_" + name))
            return cachedBooleans.get(group + "_" + name);
        for (var wini : data.getConfigs()) {
            var value = wini.get(group, name, boolean.class);
            if (value != null) {
                cachedBooleans.put(group + "_" + name, value);
                return value;
            }
        }
        return false;
    }

    /**
     * @return returns an int inside the group, and with the name
     */
    public double getDouble(String group, String name) {
        if (cachedDoubles.containsKey(group + "_" + name))
            return cachedDoubles.get(group + "_" + name);
        for (var wini : data.getConfigs()) {
            var value = wini.get(group, name, double.class);
            if (value != null) {
                cachedDoubles.put(group + "_" + name, value);
                return value;
            }
        }
        return -1;
    }

    /**
     * Resets alls of the caches
     */
    private void resetCache() {
        this.cachedBooleans.clear();
        this.cachedDoubles.clear();
        this.cachedInts.clear();
        this.cachedStrings.clear();
    }
}
