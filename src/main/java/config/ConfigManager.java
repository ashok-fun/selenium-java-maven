package config;

import core.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

public final class ConfigManager {
    private static final String DEFAULT_ENVIRONMENT = "local";
    private static final String BASE_CONFIG_RESOURCE = "config.properties";
    private static final Pattern VALID_ENVIRONMENT = Pattern.compile("[A-Za-z0-9_-]+");

    private final Properties properties;
    private final String environment;

    public ConfigManager() {
        this(loadClasspathProperties(BASE_CONFIG_RESOURCE), null, true);
    }

    public ConfigManager(String environment) {
        this(loadClasspathProperties(BASE_CONFIG_RESOURCE), environment, true);
    }

    private ConfigManager(
            Properties baseProperties, String requestedEnvironment, boolean loadEnvironmentResource) {
        this.properties = new Properties();
        this.properties.putAll(baseProperties);
        this.environment = resolveEnvironment(requestedEnvironment, baseProperties);
        if (loadEnvironmentResource) {
            this.properties.putAll(loadClasspathProperties(environmentResourceName(this.environment)));
        }
        this.properties.setProperty("environment", this.environment);
    }

    public static ConfigManager fromFiles(Path baseConfigFile, String environment) throws IOException {
        Objects.requireNonNull(baseConfigFile, "baseConfigFile must not be null");
        Properties baseProperties = loadFile(baseConfigFile);
        String selectedEnvironment = resolveEnvironment(environment, baseProperties);
        Properties mergedProperties = new Properties();
        mergedProperties.putAll(baseProperties);

        Path environmentFile = baseConfigFile.resolveSibling(environmentResourceName(selectedEnvironment));
        if (Files.exists(environmentFile)) {
            mergedProperties.putAll(loadFile(environmentFile));
        }
        return new ConfigManager(mergedProperties, selectedEnvironment, false);
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

    private static String resolveEnvironment(String requestedEnvironment, Properties baseProperties) {
        String selected = firstNonBlank(
                requestedEnvironment,
                System.getProperty("environment"),
                baseProperties.getProperty("environment"),
                DEFAULT_ENVIRONMENT);
        if (!VALID_ENVIRONMENT.matcher(selected).matches()) {
            throw new ConfigurationException("Invalid environment name: " + selected);
        }
        return selected;
    }

    private static String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.isBlank()) {
                return candidate.trim();
            }
        }
        return DEFAULT_ENVIRONMENT;
    }

    private static String environmentResourceName(String environment) {
        return "env." + environment + ".properties";
    }

    private static Properties loadClasspathProperties(String resourceName) {
        Properties loaded = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resource = classLoader == null
                ? ConfigManager.class.getClassLoader().getResourceAsStream(resourceName)
                : classLoader.getResourceAsStream(resourceName);
        if (resource == null) {
            return loaded;
        }
        try (InputStream input = resource) {
            loaded.load(input);
        } catch (IOException exception) {
            throw new ConfigurationException("Unable to load configuration resource: " + resourceName, exception);
        }
        return loaded;
    }

    private static Properties loadFile(Path path) throws IOException {
        Properties loaded = new Properties();
        try (InputStream input = Files.newInputStream(path)) {
            loaded.load(input);
        }
        return loaded;
    }
}