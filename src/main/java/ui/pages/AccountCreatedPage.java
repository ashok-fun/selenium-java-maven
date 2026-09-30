package ui.pages;

import core.WaitTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import core.BasePage;

public class AccountCreatedPage extends BasePage {
    private static final By ACCOUNT_CREATED_HEADING = By.cssSelector("h2[data-qa='account-created']");
    private static final By CONTINUE_BUTTON = By.cssSelector("a[data-qa='continue-button']");

    public AccountCreatedPage(WebDriver driver) {
        super(driver);
    }

    public boolean isAccountCreatedVisible() {
        try {
            return getText(ACCOUNT_CREATED_HEADING).toLowerCase().contains("account created");
        } catch (WaitTimeoutException exception) {
            return false;
        }
    }

    public HomePage continueToHome() {
        click(CONTINUE_BUTTON);
        return new HomePage(driver);
    }
}