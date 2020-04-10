package com.jgfx.gui;

import com.jgfx.engine.game.GameRunException;
import com.jgfx.engine.game.GameRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Tests the creation of the window
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WindowTest {

    /**
     * Sets the creation of the window
     */
    @BeforeAll
    public void testCreateGuiWindow() throws GameRunException {
        GameRunner.run(GuiGameTest.class);
    }

    @Test
    public void testIfWindowCreatedSuccessfully() {

    }
}
