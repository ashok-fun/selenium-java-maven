package core;

import java.time.Duration;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_POLLING_INTERVAL = Duration.ofMillis(200);

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Logger logger;

    protected BasePage(WebDriver driver) {
        this(driver, DEFAULT_TIMEOUT);
    }

    protected BasePage(WebDriver driver, Duration timeout) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be greater than zero");
        }
        this.wait = new WebDriverWait(driver, timeout);
        this.wait.pollingEvery(DEFAULT_POLLING_INTERVAL);
        this.logger = Logger.getLogger(getClass().getName());
    }

    protected final WebElement waitForVisible(By locator) {
        Objects.requireNonNull(locator, "locator must not be null");
        return wait.until(webDriver -> {
            try {
                WebElement element = locate(locator);
                return element.isDisplayed() ? element : null;
            } catch (NoSuchElementException | StaleElementReferenceException exception) {
                return null;
            }
        });
    }

    protected final WebElement waitForClickable(By locator) {
        Objects.requireNonNull(locator, "locator must not be null");
        return wait.until(webDriver -> {
            try {
                WebElement element = locate(locator);
                return element.isDisplayed() && element.isEnabled() ? element : null;
            } catch (NoSuchElementException | StaleElementReferenceException exception) {
                return null;
            }
        });
    }

    protected WebElement locate(By locator) {
        return driver.findElement(locator);
    }

    protected final void click(By locator) {
        waitForClickable(locator).click();
        logger.log(Level.INFO, "Clicked element: {0}", locator);
    }

    protected final void type(By locator, CharSequence text) {
        Objects.requireNonNull(text, "text must not be null");
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
        logger.log(Level.INFO, "Entered text into element: {0}", locator);
    }

    protected final String getText(By locator) {
        String text = waitForVisible(locator).getText();
        logger.log(Level.INFO, "Read text from element: {0}", locator);
        return text;
    }
}