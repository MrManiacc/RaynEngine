package com.jgfx.gui.types;

import lombok.Getter;

/**
 * Stores some default fonts
 */
public enum Fonts {
    H1("system", 24, FontWeight.MEDIUM),
    H2("system", 18, FontWeight.MEDIUM),
    H3("system", 16, FontWeight.MEDIUM),
    H4("system", 14, FontWeight.MEDIUM),
    REGULAR("system", 12, FontWeight.REGULAR);

    @Getter private String fontName;
    @Getter private int fontSize;
    @Getter private FontWeight fontWeight;

    Fonts(String fontName, int fontSize, FontWeight fontWeight) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.fontWeight = fontWeight;
    }
}
