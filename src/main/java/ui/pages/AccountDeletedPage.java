package ui.pages;

import core.WaitTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import core.BasePage;

public class AccountDeletedPage extends BasePage {
    private static final By ACCOUNT_DELETED_HEADING = By.cssSelector("h2[data-qa='account-deleted']");
    private static final By CONTINUE_BUTTON = By.cssSelector("a[data-qa='continue-button']");

    public AccountDeletedPage(WebDriver driver) {
        super(driver);
    }

    public boolean isAccountDeletedVisible() {
        try {
            return getText(ACCOUNT_DELETED_HEADING).toLowerCase().contains("account deleted");
        } catch (WaitTimeoutException exception) {
            return false;
        }
    }

    public HomePage continueToHome() {
        click(CONTINUE_BUTTON);
        return new HomePage(driver);
    }
}