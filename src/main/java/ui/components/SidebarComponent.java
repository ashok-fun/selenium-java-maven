package ui.components;

import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import core.BaseComponent;

public class SidebarComponent extends BaseComponent {
    private static final By ROOT = By.cssSelector("aside");

    public SidebarComponent(WebDriver driver) {
        this(driver, ROOT);
    }

    public SidebarComponent(WebDriver driver, By rootLocator) {
        super(driver, Objects.requireNonNull(rootLocator, "rootLocator must not be null"));
    }

    public void clickItem(String itemText) {
        click(By.linkText(Objects.requireNonNull(itemText, "itemText must not be null")));
    }

    public String getItemText(String itemText) {
        return getText(By.linkText(Objects.requireNonNull(itemText, "itemText must not be null")));
    }
}