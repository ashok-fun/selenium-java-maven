package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

public final class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    private JsonUtils() {
    }

    public static <T> T read(Path path, Class<T> targetType) throws IOException {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(targetType, "targetType must not be null");
        return OBJECT_MAPPER.readValue(path.toFile(), targetType);
    }

    public static <T> T read(Path path, TypeReference<T> targetType) throws IOException {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(targetType, "targetType must not be null");
        return OBJECT_MAPPER.readValue(path.toFile(), targetType);
    }

    public static <T> T readResource(String resourcePath, Class<T> targetType) throws IOException {
        Objects.requireNonNull(resourcePath, "resourcePath must not be null");
        Objects.requireNonNull(targetType, "targetType must not be null");
        String normalizedPath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resource = classLoader == null
                ? JsonUtils.class.getClassLoader().getResourceAsStream(normalizedPath)
                : classLoader.getResourceAsStream(normalizedPath);
        if (resource == null) {
            throw new FileNotFoundException("JSON resource not found: " + resourcePath);
        }
        try (InputStream input = resource) {
            return OBJECT_MAPPER.readValue(input, targetType);
        }
    }
}