package com.jgfx.gui.components.text;

import com.jgfx.gui.components.AbstractElementCmp;
import com.jgfx.gui.components.color.StyleColorCmp;
import com.jgfx.gui.elements.AbstractElement;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Stores data relative to the text style
 */
public class TextStyleCmp extends AbstractElementCmp {
    @Getter
    @Setter
    private boolean bold = false, italic = false, strikeThrough = false, underLine = false;

    @Getter
    private StyleColorCmp strikeThroughColor, underLineColor;

    @Setter
    @Getter
    private float kerning = 1.0f;//The spacing between characters
    @Getter
    @Setter
    private float baselineOffset = 1.0f; //The offset of the text from teh baseline

    public TextStyleCmp(AbstractElement element) {
        super(element);
    }

    /**
     * Sets the strike through color and set the strike through to true
     */
    public void setStrikeThroughColor(int r, int g, int b, int a) {
        strikeThrough = true;
        if (strikeThroughColor == null)
            strikeThroughColor = new StyleColorCmp(element());
        strikeThroughColor.setR(r);
        strikeThroughColor.setG(g);
        strikeThroughColor.setB(b);
        strikeThroughColor.setA(a);
    }

    /**
     * Sets the strike through color and set the strike through to true
     */
    public void setUnderLineColor(int r, int g, int b, int a) {
        underLine = true;
        if (underLineColor == null)
            underLineColor = new StyleColorCmp(element());
        underLineColor.setR(r);
        underLineColor.setG(g);
        underLineColor.setB(b);
        underLineColor.setA(a);
    }

    @Override
    public String dataString() {
        var strikeCol = strikeThroughColor != null ? ",\n" + strikeThroughColor.toString() : "";
        var underlineCol = underLineColor != null ? ",\n" + underLineColor.toString() : "";
        return "\"bold\": " + bold + "," +
                "\"italic\": " + italic + "," +
                "\"strikeThrough\": " + strikeThrough + "," +
                "\"underLine\": " + underLine + "," +
                "\"kerning\": " + kerning + "," +
                "\"baseLineOffset\": " + baselineOffset
                + strikeCol
                + underlineCol;
    }
}
