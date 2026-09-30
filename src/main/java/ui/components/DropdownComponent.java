package ui.components;

import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import core.BaseComponent;

public class DropdownComponent extends BaseComponent {
    private static final By ROOT = By.cssSelector("select");

    public DropdownComponent(WebDriver driver) {
        this(driver, ROOT);
    }

    public DropdownComponent(WebDriver driver, By rootLocator) {
        super(driver, Objects.requireNonNull(rootLocator, "rootLocator must not be null"));
    }

    public void selectByVisibleText(String optionText) {
        Objects.requireNonNull(optionText, "optionText must not be null");
        new Select(getRootElement()).selectByVisibleText(optionText);
        logger.info("Selected dropdown option: " + optionText);
    }

    public String getSelectedOptionText() {
        String selectedText = new Select(getRootElement()).getFirstSelectedOption().getText();
        logger.info("Read selected dropdown option");
        return selectedText;
    }
}