package com.jgfx.gui.elements.containers.stack;

import com.jgfx.gui.helpers.IAlignable;
import com.jgfx.gui.helpers.IMarginable;
import com.jgfx.gui.helpers.IPaddable;

/**
 * This class allows for stacking of objects in the z direction
 */
public class ZStack extends AbstractStack<ZStack> implements IAlignable<ZStack>, IPaddable<ZStack>, IMarginable<ZStack> {
    public ZStack() {
        super(ZStack.class);
    }
}
