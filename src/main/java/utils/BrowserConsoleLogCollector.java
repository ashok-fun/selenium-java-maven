package utils;

import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

public final class BrowserConsoleLogCollector {
    private static final Logger LOGGER = LogManager.getLogger(BrowserConsoleLogCollector.class);

    private BrowserConsoleLogCollector() {
    }

    public static List<LogEntry> collect(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("driver must not be null");
        }
        try {
            return driver.manage().logs().get(LogType.BROWSER).getAll();
        } catch (WebDriverException exception) {
            LOGGER.warn("Browser console logs are unavailable", exception);
            return List.of();
        }
    }

    public static List<String> collectSevereMessages(WebDriver driver) {
        return collect(driver).stream()
                .filter(entry -> entry.getLevel().getName().equals("SEVERE"))
                .map(LogEntry::getMessage)
                .toList();
    }

    public static Path saveBesideReport(Path individualReportPath, WebDriver driver) throws IOException {
        Objects.requireNonNull(individualReportPath, "individualReportPath must not be null");
        String reportFileName = individualReportPath.getFileName().toString();
        int extensionIndex = reportFileName.lastIndexOf('.');
        String logFileName = extensionIndex < 0
                ? reportFileName + ".txt"
                : reportFileName.substring(0, extensionIndex) + ".txt";
        Path logPath = individualReportPath.resolveSibling(logFileName);

        String logContent;
        if (driver == null) {
            logContent = "Browser console logs unavailable: WebDriver was not initialized.";
        } else {
            List<LogEntry> entries = collect(driver);
            logContent = entries.isEmpty()
                    ? "No browser console logs were captured."
                    : entries.stream()
                            .map(BrowserConsoleLogCollector::formatEntry)
                            .collect(Collectors.joining(System.lineSeparator()));
        }
        Files.writeString(logPath, logContent + System.lineSeparator(), StandardCharsets.UTF_8);
        return logPath;
    }

    private static String formatEntry(LogEntry entry) {
        return Instant.ofEpochMilli(entry.getTimestamp()) + " ["
                + entry.getLevel().getName() + "] " + entry.getMessage();
    }
}