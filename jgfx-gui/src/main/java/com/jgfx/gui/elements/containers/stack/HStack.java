package com.jgfx.gui.elements.stack;


import com.jgfx.gui.helpers.IAlignable;
import com.jgfx.gui.helpers.IMarginable;
import com.jgfx.gui.helpers.IPaddable;

/**
 * This class allows for stacking of objects horizontally
 */
public class HStack extends AbstractStack<HStack> implements IAlignable<HStack>, IPaddable<HStack>, IMarginable<HStack> {

    public HStack() {
        super(HStack.class);
    }

}
