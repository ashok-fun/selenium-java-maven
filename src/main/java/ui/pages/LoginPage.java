package ui.pages;

import core.WaitTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import core.BasePage;

public class LoginPage extends BasePage {
    private static final By LOGIN_HEADING = By.cssSelector(".login-form h2");
    private static final By LOGIN_EMAIL = By.cssSelector("input[data-qa='login-email']");
    private static final By LOGIN_PASSWORD = By.cssSelector("input[data-qa='login-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[data-qa='login-button']");
    private static final By LOGIN_SIGNUP_SEPARATOR = By.xpath("//h2[normalize-space()='OR']");
    private static final By NEW_USER_HEADING = By.cssSelector(".signup-form h2");
    private static final By SIGNUP_NAME = By.cssSelector("input[data-qa='signup-name']");
    private static final By SIGNUP_EMAIL = By.cssSelector("input[data-qa='signup-email']");
    private static final By SIGNUP_BUTTON = By.cssSelector("button[data-qa='signup-button']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoginFormVisible() {
        try {
            return getText(LOGIN_HEADING).trim().equals("Login to your account");
        } catch (WaitTimeoutException exception) {
            return false;
        }
    }

    public boolean isLoginSignupSeparatorVisible() {
        try {
            return getText(LOGIN_SIGNUP_SEPARATOR).trim().equals("OR");
        } catch (WaitTimeoutException exception) {
            return false;
        }
    }

    public boolean isNewUserSignupVisible() {
        try {
            return getText(NEW_USER_HEADING).trim().equals("New User Signup!");
        } catch (WaitTimeoutException exception) {
            return false;
        }
    }

    public void login(String email, String password) {
        type(LOGIN_EMAIL, email);
        type(LOGIN_PASSWORD, password);
        click(LOGIN_BUTTON);
    }

    public SignupPage startSignup(String name, String email) {
        type(SIGNUP_NAME, name);
        type(SIGNUP_EMAIL, email);
        click(SIGNUP_BUTTON);
        return new SignupPage(driver);
    }
}