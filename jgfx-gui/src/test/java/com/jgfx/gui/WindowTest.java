package com.jgfx.gui;

import com.jgfx.GuiGameTest;
import com.jgfx.engine.game.GameRunException;
import com.jgfx.engine.game.GameRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Tests the creation of the window
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WindowTest {
    private static Logger logger = LogManager.getLogger(WindowTest.class);

    @BeforeAll
    public void createMainGameEngineInstanceForTesting() throws GameRunException {
        GameRunner.run(GuiGameTest.class);
    }

    @Test
    public void checkIfOpenGlContextExists() {

    }
}
