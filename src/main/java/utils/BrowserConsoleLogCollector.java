package utils;

import java.util.List;
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
}