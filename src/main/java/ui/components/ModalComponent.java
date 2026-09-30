package ui.components;

import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import core.BaseComponent;

public class ModalComponent extends BaseComponent {
    private static final By ROOT = By.cssSelector("[role='dialog'], .modal");
    private static final By CLOSE_BUTTON = By.cssSelector("button[aria-label='Close'], .close");
    private static final By BODY = By.cssSelector(".modal-body");

    public ModalComponent(WebDriver driver) {
        this(driver, ROOT);
    }

    public ModalComponent(WebDriver driver, By rootLocator) {
        super(driver, Objects.requireNonNull(rootLocator, "rootLocator must not be null"));
    }

    public boolean isVisible() {
        return isRootVisible();
    }

    public String getBodyText() {
        return getText(BODY);
    }

    public void close() {
        click(CLOSE_BUTTON);
    }
}