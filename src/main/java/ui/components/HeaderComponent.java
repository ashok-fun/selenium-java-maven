package ui.components;

import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import core.BaseComponent;

public class HeaderComponent extends BaseComponent {
    private static final By ROOT = By.cssSelector("header");

    public HeaderComponent(WebDriver driver) {
        this(driver, ROOT);
    }

    public HeaderComponent(WebDriver driver, By rootLocator) {
        super(driver, Objects.requireNonNull(rootLocator, "rootLocator must not be null"));
    }

    public void clickNavigationLink(String linkText) {
        click(By.linkText(Objects.requireNonNull(linkText, "linkText must not be null")));
    }

    public String getNavigationLinkText(String linkText) {
        return getText(By.linkText(Objects.requireNonNull(linkText, "linkText must not be null")));
    }
}