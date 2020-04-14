package com.jgfx.gui.components.text;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.enums.FontWeight;
import com.jgfx.gui.enums.Fonts;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores the information about a font
 */
public class FontCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private String fontName = Fonts.REGULAR.getFontName();
    @Getter
    @Setter
    private float fontSize = Fonts.REGULAR.getFontSize();
    @Getter
    @Setter
    private FontWeight fontWeight = Fonts.REGULAR.getFontWeight();

    public FontCmp(AbstractElement parent) {
        super(parent);
    }

    @Override
    public String dataString() {
        return "\"fontName\": " + "\"" + fontName + "\"" + "," +
                "\"fontSize\": " + fontSize + "," +
                "\"fontWeight\": " + "\"" + fontWeight.name().toLowerCase() + "\"";
    }
}
