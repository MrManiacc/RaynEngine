package com.jgfx.engine;

import com.jgfx.engine.game.GameRunException;
import com.jgfx.engine.game.GameRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameEngineTest {

    @Test
    public void testGameEngineDiscovery() {
        try {
            var test = GameRunner.run(TestGame.class);
            Assertions.assertEquals(1, test.getMappedSubsystems().size());
        } catch (GameRunException e) {
            e.printStackTrace();
        }
    }
}
