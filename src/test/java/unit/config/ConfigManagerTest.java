package unit.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.ConfigurationException;
import config.ConfigManager;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigManagerTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadsBundledEnvironmentResources() {
        ConfigManager local = new ConfigManager("local");
        ConfigManager dev = new ConfigManager("dev");
        ConfigManager qa = new ConfigManager("qa");

        assertEquals("https://automationexercise.com/", local.getProperties().getProperty("baseUrl"));
        assertEquals("http://localhost:8080", dev.getProperties().getProperty("baseUrl"));
        assertEquals("https://automationexercise.com", qa.getProperties().getProperty("baseUrl"));
        assertEquals("qa", qa.getEnvironment());
    }

    @Test
    void mergesEnvironmentPropertiesAndExposesTypedValues() throws Exception {
        Path baseConfig = temporaryDirectory.resolve("config.properties");
        Files.writeString(baseConfig, "environment=dev\nbaseUrl=https://base.example\ntimeout=10\nenabled=true\n");
        Files.writeString(
                temporaryDirectory.resolve("env.qa.properties"),
                "baseUrl=https://qa.example\ntimeout=25\nenabled=false\nwait=3000\n");

        ConfigManager config = ConfigManager.fromFiles(baseConfig, "qa");

        assertEquals("qa", config.getEnvironment());
        assertEquals("qa", config.getString("environment"));
        assertEquals("https://qa.example", config.getBaseUrl());
        assertEquals(25, config.getInt("timeout"));
        assertEquals(3000L, config.getLong("wait", 1000L));
        assertFalse(config.getBoolean("enabled"));
        assertTrue(config.getBoolean("missing.flag", true));
    }

    @Test
    void systemPropertiesOverrideFilesWithoutChangingExplicitEnvironment() throws Exception {
        Path baseConfig = temporaryDirectory.resolve("config.properties");
        Files.writeString(baseConfig, "environment=dev\nport=8080\n");
        String previousEnvironment = System.getProperty("environment");
        String previousPort = System.getProperty("port");
        System.setProperty("environment", "prod");
        System.setProperty("port", "9090");
        try {
            ConfigManager config = ConfigManager.fromFiles(baseConfig, "qa");

            assertEquals("qa", config.getEnvironment());
            assertEquals("qa", config.getString("environment"));
            assertEquals(9090, config.getInt("port"));
        } finally {
            restoreProperty("environment", previousEnvironment);
            restoreProperty("port", previousPort);
        }
    }

    @Test
    void rejectsMissingAndMalformedRequiredValues() throws Exception {
        Path baseConfig = temporaryDirectory.resolve("config.properties");
        Files.writeString(baseConfig, "attempts=many\nactive=yes\n");
        ConfigManager config = ConfigManager.fromFiles(baseConfig, "local");

        assertThrows(ConfigurationException.class, () -> config.getString("missing"));
        assertThrows(ConfigurationException.class, () -> config.getInt("attempts"));
        assertThrows(ConfigurationException.class, () -> config.getBoolean("active"));
        assertThrows(ConfigurationException.class,
                () -> ConfigManager.fromFiles(baseConfig, "../outside"));
    }

    private void restoreProperty(String key, String previousValue) {
        if (previousValue == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, previousValue);
        }
    }
}