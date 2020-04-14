package com.jgfx.gui.enums;

import lombok.Getter;

/**
 * Some default colors
 */
public enum Colors {
    TURQUOISE("#1abc9c"),
    EMERALD("#2ecc71"),
    PETER_RIVER("#3498db"),
    AMETHYST("#9b59b6"),
    WET_ASPHALT("#34495e"),
    GREEN_SEA("#16a085"),
    NEPHRITIS("#27ae60"),
    BELIZE_HOLE("#16a085"),
    WISTERIA("#8e44ad"),
    MIDNIGHT_BLUE("#2c3e50"),
    SUN_FLOWER("#f1c40f"),
    CARROT("#e67e22"),
    ALIZARIN("#e74c3c"),
    CLOUDS("#ecf0f1"),
    CONCRETE("#95a5a6"),
    ORANGE_PEEL("#f39c12"),
    PUMPKIN("#d35400"),
    POMEGRANATE("#c0392b"),
    SILVER("#f1c40f"),
    ASBESTOS("#7f8c8d"),
    BLACK("#000000"),
    WHITE("#ffffff");
    @Getter private final String hex;

    Colors(String hex) {
        this.hex = hex;
    }
}
