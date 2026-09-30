package core;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class BaseComponent extends BasePage {
    private final By rootLocator;

    protected BaseComponent(WebDriver driver, By rootLocator) {
        super(driver);
        this.rootLocator = Objects.requireNonNull(rootLocator, "rootLocator must not be null");
    }

    protected BaseComponent(WebDriver driver, By rootLocator, Duration timeout) {
        super(driver, timeout);
        this.rootLocator = Objects.requireNonNull(rootLocator, "rootLocator must not be null");
    }

    protected final WebElement getRootElement() {
        return wait.until(webDriver -> {
            try {
                WebElement root = webDriver.findElement(rootLocator);
                return root.isDisplayed() ? root : null;
            } catch (NoSuchElementException | StaleElementReferenceException exception) {
                return null;
            }
        });
    }

    protected final boolean isRootVisible() {
        try {
            return driver.findElements(rootLocator).stream().anyMatch(WebElement::isDisplayed);
        } catch (StaleElementReferenceException exception) {
            return false;
        }
    }

    protected final List<WebElement> findElements(By locator) {
        Objects.requireNonNull(locator, "locator must not be null");
        return getRootElement().findElements(locator);
    }

    @Override
    protected WebElement locate(By locator) {
        return driver.findElement(rootLocator).findElement(locator);
    }
}