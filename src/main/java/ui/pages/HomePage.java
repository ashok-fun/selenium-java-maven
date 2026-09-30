package ui.pages;

import java.util.Objects;
import java.util.logging.Level;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import core.BasePage;
import ui.components.HeaderComponent;

public class HomePage extends BasePage {
    public static final String URL = "http://automationexercise.com";

    private static final By HOME_LOGO = By.cssSelector(
            "a[href='/'] img[alt='Website for automation practice']");
    private static final By LOGGED_IN_STATUS = By.xpath(
            "//header//a[contains(normalize-space(.), 'Logged in as')]");
    private static final By HEADER = By.id("header");

    private final HeaderComponent header;

    public HomePage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver, HEADER);
    }

    public HomePage open() {
        driver.get(URL);
        logger.log(Level.INFO, "Navigated to AutomationExercise: {0}", URL);
        return this;
    }

    public boolean isHomePageVisible() {
        try {
            waitForVisible(HOME_LOGO);
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public LoginPage clickSignupLogin() {
        header.clickNavigationLink("Signup / Login");
        return new LoginPage(driver);
    }

    public boolean isLoggedInAs(String username) {
        Objects.requireNonNull(username, "username must not be null");
        try {
            String status = getText(LOGGED_IN_STATUS).replaceAll("\\s+", " ").trim();
            return status.equals("Logged in as " + username);
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public AccountDeletedPage deleteAccount() {
        header.clickNavigationLink("Delete Account");
        return new AccountDeletedPage(driver);
    }
}