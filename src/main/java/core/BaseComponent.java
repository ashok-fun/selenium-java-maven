package core;

import java.time.Duration;
import java.util.Objects;
import org.openqa.selenium.By;
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

    @Override
    protected WebElement locate(By locator) {
        return driver.findElement(rootLocator).findElement(locator);
    }
}