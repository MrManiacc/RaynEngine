package com.jgfx.gui.elements.stack;

import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.helpers.*;

import java.util.Optional;

/**
 * This class allows for stacking of objects vertically
 */
public class VStack extends AbstractStack<VStack> implements IAlignable<VStack>, IPaddable<VStack>, IMarginable<VStack>, IColorable<VStack>, ITexturable<VStack> {
    public VStack() {
        super(VStack.class);
    }
}
