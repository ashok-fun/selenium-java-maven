package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

public final class FileDownloadValidator {
    private FileDownloadValidator() {
    }

    public static DownloadInfo validate(Path path, long minimumSizeBytes, String expectedExtension)
            throws IOException {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(expectedExtension, "expectedExtension must not be null");
        if (minimumSizeBytes < 0) {
            throw new IllegalArgumentException("minimumSizeBytes must not be negative");
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("Downloaded file does not exist or is not a regular file: " + path);
        }

        long sizeBytes = Files.size(path);
        if (sizeBytes < minimumSizeBytes) {
            throw new IOException("Downloaded file is smaller than the minimum size: " + path);
        }

        String actualExtension = extensionOf(path);
        String normalizedExpectedExtension = normalizeExtension(expectedExtension);
        if (!actualExtension.equals(normalizedExpectedExtension)) {
            throw new IOException("Expected file extension ." + normalizedExpectedExtension
                    + " but found ." + actualExtension + " for " + path);
        }
        return new DownloadInfo(path.toAbsolutePath(), sizeBytes, actualExtension);
    }

    private static String extensionOf(Path path) {
        String fileName = path.getFileName().toString();
        int extensionStart = fileName.lastIndexOf('.');
        return extensionStart < 0 ? "" : fileName.substring(extensionStart + 1).toLowerCase(Locale.ROOT);
    }

    private static String normalizeExtension(String extension) {
        String normalized = extension.trim().toLowerCase(Locale.ROOT);
        return normalized.startsWith(".") ? normalized.substring(1) : normalized;
    }

    public record DownloadInfo(Path path, long sizeBytes, String extension) {
    }
}