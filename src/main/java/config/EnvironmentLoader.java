package config;

import core.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Pattern;

public final class EnvironmentLoader {
    private static final String DEFAULT_ENVIRONMENT = "local";
    private static final Pattern VALID_ENVIRONMENT = Pattern.compile("[A-Za-z0-9_-]+");

    private EnvironmentLoader() {
    }

    public static Properties loadClasspath(String baseResource, String requestedEnvironment) {
        Properties baseProperties = loadClasspathResource(baseResource);
        String environment = resolveEnvironment(requestedEnvironment, baseProperties);
        Properties properties = copyOf(baseProperties);
        properties.putAll(loadClasspathResource(environmentResourceName(environment)));
        properties.setProperty("environment", environment);
        return properties;
    }

    public static Properties loadFiles(Path baseConfigFile, String requestedEnvironment) throws IOException {
        Properties baseProperties = loadFile(baseConfigFile);
        String environment = resolveEnvironment(requestedEnvironment, baseProperties);
        Properties properties = copyOf(baseProperties);
        Path environmentFile = baseConfigFile.resolveSibling(environmentResourceName(environment));
        if (Files.exists(environmentFile)) {
            properties.putAll(loadFile(environmentFile));
        }
        properties.setProperty("environment", environment);
        return properties;
    }

    private static String resolveEnvironment(String requestedEnvironment, Properties baseProperties) {
        String environment = firstNonBlank(
                requestedEnvironment,
                System.getProperty("environment"),
                baseProperties.getProperty("environment"),
                DEFAULT_ENVIRONMENT);
        if (!VALID_ENVIRONMENT.matcher(environment).matches()) {
            throw new ConfigurationException("Invalid environment name: " + environment);
        }
        return environment;
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

    private static Properties loadClasspathResource(String resourceName) {
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resource = classLoader == null
                ? EnvironmentLoader.class.getClassLoader().getResourceAsStream(resourceName)
                : classLoader.getResourceAsStream(resourceName);
        if (resource == null) {
            return properties;
        }
        try (InputStream input = resource) {
            properties.load(input);
        } catch (IOException exception) {
            throw new ConfigurationException("Unable to load configuration resource: " + resourceName, exception);
        }
        return properties;
    }

    private static Properties loadFile(Path path) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);
        }
        return properties;
    }

    private static Properties copyOf(Properties source) {
        Properties copy = new Properties();
        copy.putAll(source);
        return copy;
    }
}