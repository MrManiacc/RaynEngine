package com.jgfx.assets;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.net.URL;
import java.util.Objects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigTest {
    private File logsFolder;

    @BeforeAll
    public void testResourcesForConfigFolders() {
        logsFolder = getResourceFromResourcesByName("logs");
        assertNotNull(logsFolder);
    }

    private File getResourceFromResourcesByName(String name) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(name);
        String path = Objects.requireNonNull(url).getPath();
        return new File(path);
    }

    @Test
    @Order(1)
    public void testIfLog4jConfigFileIsPresent() {
        var logConfig = getResourceFromResourcesByName("log4j2.xml");
        assertNotNull(logConfig);
        assertTrue(logConfig.exists());
        assertTrue(logConfig.isFile());
    }
}
