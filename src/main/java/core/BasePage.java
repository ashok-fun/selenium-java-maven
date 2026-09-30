package core;

import java.time.Duration;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class BasePage {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_POLLING_INTERVAL = Duration.ofMillis(200);

    protected final WebDriver driver;
    protected final FluentWaitUtils wait;
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
        this.wait = new FluentWaitUtils(driver, timeout, DEFAULT_POLLING_INTERVAL);
        this.logger = LogManager.getLogger(getClass());
    }

    protected final WebElement waitForVisible(By locator) {
        Objects.requireNonNull(locator, "locator must not be null");
        return wait.untilVisible(locator, this::locate);
    }

    protected final WebElement waitForClickable(By locator) {
        Objects.requireNonNull(locator, "locator must not be null");
        return wait.untilClickable(locator, this::locate);
    }

    protected WebElement locate(By locator) {
        return driver.findElement(locator);
    }

    protected final void click(By locator) {
        waitForClickable(locator).click();
        logger.info("Clicked element: {}", locator);
    }

    protected final void type(By locator, CharSequence text) {
        Objects.requireNonNull(text, "text must not be null");
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
        logger.info("Entered text into element: {}", locator);
    }

    protected final String getText(By locator) {
        String text = waitForVisible(locator).getText();
        logger.info("Read text from element: {}", locator);
        return text;
    }
}