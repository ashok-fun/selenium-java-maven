package ui.components;

import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import core.BaseComponent;

public class ToastComponent extends BaseComponent {
    private static final By ROOT = By.cssSelector("[role='alert']");

    public ToastComponent(WebDriver driver) {
        this(driver, ROOT);
    }

    public ToastComponent(WebDriver driver, By rootLocator) {
        super(driver, Objects.requireNonNull(rootLocator, "rootLocator must not be null"));
    }

    public boolean isVisible() {
        return isRootVisible();
    }

    public String getMessage() {
        String message = getRootElement().getText();
        logger.info("Read toast notification message");
        return message;
    }
}