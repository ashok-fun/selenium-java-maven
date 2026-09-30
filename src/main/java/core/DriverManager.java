package core;

import java.util.Locale;
import java.util.Objects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class DriverManager {
    private static final String DEFAULT_BROWSER = "chrome";
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            driver = createDriver(System.getProperty("browser", DEFAULT_BROWSER));
            setDriver(driver);
        }
        return driver;
    }

    public static WebDriver getDriver(String browserName) {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            driver = createDriver(browserName);
            setDriver(driver);
        }
        return driver;
    }

    public static void setDriver(WebDriver driver) {
        DRIVER.set(Objects.requireNonNull(driver, "driver must not be null"));
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }

    private static WebDriver createDriver(String browserName) {
        if (browserName == null || browserName.isBlank()) {
            throw new IllegalArgumentException("Browser name must not be blank");
        }

        return switch (browserName.trim().toLowerCase(Locale.ROOT)) {
            case "chrome" -> new ChromeDriver(new ChromeOptions());
            case "firefox" -> new FirefoxDriver(new FirefoxOptions());
            case "edge" -> new EdgeDriver(new EdgeOptions());
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browserName + ". Supported browsers: chrome, firefox, edge");
        };
    }
}