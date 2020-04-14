package com.jgfx.gui.elements.containers.stack;

import com.jgfx.gui.components.container.SpacingCmp;
import com.jgfx.gui.helpers.*;

import java.util.Optional;

/**
 * This class allows for stacking of objects vertically
 */
public class VStack extends AbstractStack<VStack> implements IAlignable<VStack>, IPaddable<VStack>, IMarginable<VStack>, IColorable<VStack>, ITexturable<VStack> {
    /**
     * Stacks can have no spacing by default to
     */
    public VStack() {
        super(VStack.class);
    }

    /**
     * Stacks can have spacing by default
     */
    public VStack(float spacing) {
        super(VStack.class, spacing);
    }

}
