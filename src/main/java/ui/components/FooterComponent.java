package ui.components;

import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import core.BaseComponent;

public class FooterComponent extends BaseComponent {
    private static final By ROOT = By.cssSelector("footer");

    public FooterComponent(WebDriver driver) {
        this(driver, ROOT);
    }

    public FooterComponent(WebDriver driver, By rootLocator) {
        super(driver, Objects.requireNonNull(rootLocator, "rootLocator must not be null"));
    }

    public String getText() {
        WebElement footer = getRootElement();
        logger.info("Read footer text");
        return footer.getText();
    }

    public void clickLink(String linkText) {
        click(By.linkText(Objects.requireNonNull(linkText, "linkText must not be null")));
    }
}