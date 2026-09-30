package core;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;

public final class FluentWaitUtils {
    private static final Duration DEFAULT_POLLING_INTERVAL = Duration.ofMillis(200);

    private final WebDriver driver;
    private final Duration timeout;
    private final Duration pollingInterval;

    public FluentWaitUtils(WebDriver driver, Duration timeout) {
        this(driver, timeout, DEFAULT_POLLING_INTERVAL);
    }

    public FluentWaitUtils(WebDriver driver, Duration timeout, Duration pollingInterval) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.timeout = requirePositive(timeout, "timeout");
        this.pollingInterval = requirePositive(pollingInterval, "pollingInterval");
    }

    public <T> T until(Function<WebDriver, T> condition) {
        Objects.requireNonNull(condition, "condition must not be null");
        try {
            return new FluentWait<>(driver)
                    .withTimeout(timeout)
                    .pollingEvery(pollingInterval)
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .until(condition);
        } catch (TimeoutException exception) {
            throw new WaitTimeoutException("Condition was not met within " + timeout, exception);
        }
    }

    public WebElement untilVisible(By locator) {
        return untilVisible(locator, by -> driver.findElement(by));
    }

    public WebElement untilVisible(By locator, Function<By, WebElement> elementFinder) {
        Objects.requireNonNull(locator, "locator must not be null");
        Objects.requireNonNull(elementFinder, "elementFinder must not be null");
        return until(webDriver -> {
            WebElement element = elementFinder.apply(locator);
            return element.isDisplayed() ? element : null;
        });
    }

    public WebElement untilClickable(By locator) {
        return untilClickable(locator, by -> driver.findElement(by));
    }

    public WebElement untilClickable(By locator, Function<By, WebElement> elementFinder) {
        Objects.requireNonNull(locator, "locator must not be null");
        Objects.requireNonNull(elementFinder, "elementFinder must not be null");
        return until(webDriver -> {
            WebElement element = elementFinder.apply(locator);
            return element.isDisplayed() && element.isEnabled() ? element : null;
        });
    }

    private static Duration requirePositive(Duration value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        if (value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
        return value;
    }
}