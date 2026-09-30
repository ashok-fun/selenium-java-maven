package config;

import core.ConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public final class ConfigManager {
    private static final String DEFAULT_ENVIRONMENT = "local";
    private static final String BASE_CONFIG_RESOURCE = "config.properties";

    private final Properties properties;
    private final String environment;

    public ConfigManager() {
        this(EnvironmentLoader.loadClasspath(BASE_CONFIG_RESOURCE, null));
    }

    public ConfigManager(String environment) {
        this(EnvironmentLoader.loadClasspath(BASE_CONFIG_RESOURCE, environment));
    }

    private ConfigManager(Properties properties) {
        this.properties = new Properties();
        this.properties.putAll(properties);
        this.environment = properties.getProperty("environment", DEFAULT_ENVIRONMENT);
    }

    public static ConfigManager fromFiles(Path baseConfigFile, String environment) throws IOException {
        Objects.requireNonNull(baseConfigFile, "baseConfigFile must not be null");
        return new ConfigManager(EnvironmentLoader.loadFiles(baseConfigFile, environment));
    }

    public String getEnvironment() {
        return environment;
    }

    public String getBaseUrl() {
        String baseUrl = getString("baseUrl", "");
        return baseUrl.isBlank() ? getString("base.url", "") : baseUrl;
    }

    public String getString(String key) {
        Objects.requireNonNull(key, "key must not be null");
        if (key.equals("environment")) {
            return environment;
        }
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        if (value == null) {
            throw new ConfigurationException("Required configuration property is missing: " + key);
        }
        return value.trim();
    }

    public String getString(String key, String defaultValue) {
        Objects.requireNonNull(key, "key must not be null");
        if (key.equals("environment")) {
            return environment;
        }
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key, defaultValue);
        }
        return value == null ? null : value.trim();
    }

    public int getInt(String key) {
        String value = getString(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new ConfigurationException("Configuration property '" + key + "' must be an integer", exception);
        }
    }

    public int getInt(String key, int defaultValue) {
        String value = getString(key, null);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new ConfigurationException("Configuration property '" + key + "' must be an integer", exception);
        }
    }

    public long getLong(String key, long defaultValue) {
        String value = getString(key, null);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new ConfigurationException("Configuration property '" + key + "' must be a long", exception);
        }
    }

    public boolean getBoolean(String key) {
        String value = getString(key);
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw new ConfigurationException("Configuration property '" + key + "' must be true or false");
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key, null);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw new ConfigurationException("Configuration property '" + key + "' must be true or false");
    }

    public Properties getProperties() {
        Properties copy = new Properties();
        copy.putAll(properties);
        return copy;
    }

}