package utils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

public final class BrowserConsoleLogCollector {
    private static final Logger LOGGER = Logger.getLogger(BrowserConsoleLogCollector.class.getName());

    private BrowserConsoleLogCollector() {
    }

    public static List<LogEntry> collect(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("driver must not be null");
        }
        try {
            return driver.manage().logs().get(LogType.BROWSER).getAll();
        } catch (WebDriverException exception) {
            LOGGER.log(Level.WARNING, "Browser console logs are unavailable", exception);
            return List.of();
        }
    }

    public static List<String> collectSevereMessages(WebDriver driver) {
        return collect(driver).stream()
                .filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue())
                .map(LogEntry::getMessage)
                .toList();
    }
}