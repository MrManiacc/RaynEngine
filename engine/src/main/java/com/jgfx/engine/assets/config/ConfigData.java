package com.jgfx.engine.assets.config;

import com.jgfx.assets.data.AssetData;
import lombok.Getter;
import org.ini4j.Wini;

import java.util.List;

public class ConfigData implements AssetData {
    @Getter private List<Wini> configs;

    public ConfigData(List<Wini> configs) {
        this.configs = configs;
    }
}
