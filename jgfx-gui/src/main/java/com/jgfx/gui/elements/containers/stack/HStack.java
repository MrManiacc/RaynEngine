package com.jgfx.gui.elements.containers.stack;


import com.jgfx.gui.helpers.IAlignable;
import com.jgfx.gui.helpers.IMarginable;
import com.jgfx.gui.helpers.IPaddable;

/**
 * This class allows for stacking of objects horizontally
 */
public class HStack extends AbstractStack<HStack> implements IAlignable<HStack>, IPaddable<HStack>, IMarginable<HStack> {

    /**
     * Stacks can have no spacing by default to
     */
    public HStack() {
        super(HStack.class);
    }

    /**
     * Stacks can have spacing by default
     */
    public HStack(float spacing) {
        super(HStack.class, spacing);
    }

}
