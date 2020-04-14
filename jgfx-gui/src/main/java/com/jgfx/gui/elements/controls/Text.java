package com.jgfx.gui.elements.controls;

import com.jgfx.gui.components.text.FontCmp;
import com.jgfx.gui.components.text.TextCmp;
import com.jgfx.gui.components.text.TextStyleCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.helpers.IAlignable;
import com.jgfx.gui.helpers.IColorable;
import com.jgfx.gui.helpers.IMarginable;
import com.jgfx.gui.helpers.IPaddable;
import com.jgfx.gui.enums.FontWeight;
import com.jgfx.gui.enums.Fonts;

/**
 * This will create some form of text that can be rendered
 */
public class Text extends AbstractElement<Text> implements IColorable<Text>, IAlignable<Text>, IPaddable<Text>, IMarginable<Text> {

    /**
     * Text requires a string by default
     */
    public Text(String text) {
        super(Text.class);
        //We need text and a font by default
        text(text);
        font(Fonts.REGULAR);
    }



    /**
     * This will set the text component for this element
     */
    public Text text(String text) {
        get(TextCmp.class).ifPresentOrElse(textCmp -> textCmp.setText(text), () -> {
            var textCmp = new TextCmp(this);
            textCmp.setText(text);
            add(textCmp);
        });
        return this;
    }

    /**
     * Sets the font for the component
     */
    public Text font(String fontName, int fontSize, FontWeight fontWeight) {
        get(FontCmp.class).ifPresentOrElse(fontCmp -> {
            if (fontName != null)
                fontCmp.setFontName(fontName);
            if (fontSize != -1)
                fontCmp.setFontSize(fontSize);
            if (fontWeight != null)
                fontCmp.setFontWeight(fontWeight);
        }, () -> {
            var fontCmp = new FontCmp(this);
            if (fontName != null)
                fontCmp.setFontName(fontName);
            if (fontSize != -1)
                fontCmp.setFontSize(fontSize);
            if (fontWeight != null)
                fontCmp.setFontWeight(fontWeight);
            add(fontCmp);
        });
        return this;
    }


    /**
     * Sets the font to the given default font
     */
    public Text font(Fonts font) {
        return font(font.getFontName(), font.getFontSize(), font.getFontWeight());
    }

    /**
     * Sets the font to the given fontName and fontSize, while retaining the fontWeight
     */
    public Text font(String fontName, int fontSize) {
        return font(fontName, fontSize, null);
    }

    /**
     * Sets the font to the given fontName and fontSize, while retaining the fontWeight
     */
    public Text font(int fontSize, FontWeight fontWeight) {
        return font(null, fontSize, fontWeight);
    }

    /**
     * Sets the font to the given fontName and fontSize, while retaining the fontWeight
     */
    public Text font(String fontName, FontWeight fontWeight) {
        return font(fontName, -1, fontWeight);
    }


    /**
     * Sets the font to the given fontName and fontSize, while retaining the fontWeight
     */
    public Text font(int fontSize) {
        return font(null, fontSize, null);
    }

    /**
     * Sets the font to the given fontName and fontSize, while retaining the fontWeight
     */
    public Text font(FontWeight fontWeight) {
        return font(null, -1, fontWeight);
    }

    /**
     * Sets the font to bold or not bold
     */
    public Text bold(boolean bold) {
        get(TextStyleCmp.class).ifPresentOrElse(styleCmp -> styleCmp.setBold(bold), () -> {
            var styleCmp = new TextStyleCmp(this);
            styleCmp.setBold(bold);
            add(styleCmp);
        });
        return this;
    }

    /**
     * Sets the text to bold
     */
    public Text bold() {
        return bold(true);
    }

    /**
     * Sets the font to italic or non italic
     */
    public Text italic(boolean italic) {
        get(TextStyleCmp.class).ifPresentOrElse(styleCmp -> styleCmp.setItalic(italic), () -> {
            var styleCmp = new TextStyleCmp(this);
            styleCmp.setItalic(italic);
            add(styleCmp);
        });
        return this;
    }

    /**
     * Sets the text to italic
     */
    public Text italic() {
        return italic(true);
    }

    /**
     * Sets the strike through to the given color if true or removes it if false
     */
    public Text strikeThrough(boolean strikeThrough, int r, int g, int b, int a) {
        get(TextStyleCmp.class).ifPresentOrElse(styleCmp -> {
                    if (strikeThrough)
                        styleCmp.setStrikeThroughColor(r, g, b, a);
                    else
                        styleCmp.setStrikeThrough(false);
                }
                , () -> {
                    var styleCmp = new TextStyleCmp(this);
                    if (strikeThrough)
                        styleCmp.setStrikeThroughColor(r, g, b, a);
                    else
                        styleCmp.setStrikeThrough(false);
                    add(styleCmp);
                });
        return this;
    }

    /**
     * This will apply strike through, using a the given color
     */
    public Text strikeThrough(int r, int g, int b, int a) {
        return strikeThrough(true, r, g, b, a);
    }

    /**
     * This will either apply strike through or remove it, using a default color of black
     */
    public Text strikeThrough(boolean strikeThrough) {
        return strikeThrough(strikeThrough, 0, 0, 0, 255);
    }

    /**
     * This will apply a black strike through some text
     */
    public Text strikeThrough() {
        return strikeThrough(true);
    }

    /**
     * Sets the underline through to the given color if true or removes it if false
     */
    public Text underLine(boolean underLine, int r, int g, int b, int a) {
        get(TextStyleCmp.class).ifPresentOrElse(styleCmp -> {
                    if (underLine)
                        styleCmp.setUnderLineColor(r, g, b, a);
                    else
                        styleCmp.setUnderLine(false);
                }
                , () -> {
                    var styleCmp = new TextStyleCmp(this);
                    if (underLine)
                        styleCmp.setUnderLineColor(r, g, b, a);
                    else
                        styleCmp.setUnderLine(false);
                    add(styleCmp);
                });
        return this;
    }

    /**
     * This will apply strike through, using a the given color
     */
    public Text underLine(int r, int g, int b, int a) {
        return underLine(true, r, g, b, a);
    }

    /**
     * This will either apply strike through or remove it, using a default color of black
     */
    public Text underLine(boolean underLine) {
        return underLine(underLine, 0, 0, 0, 255);
    }

    /**
     * This will apply a black strike through some text
     */
    public Text underLine() {
        return underLine(true);
    }


}
