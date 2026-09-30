package unit.ui.pages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ui.pages.AccountCreatedPage;
import ui.pages.AccountDeletedPage;
import ui.pages.HomePage;
import ui.pages.LoginPage;
import ui.pages.SignupPage;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class AutomationExercisePagesTest {
    @Test
    void homeAndSignupPagesSupportRegistrationEntryFlow() {
        List<String> actions = new ArrayList<>();
        HomePage homePage = new HomePage(createDriver(actions));

        assertTrue(homePage.open().isHomePageVisible());
        LoginPage loginPage = homePage.clickSignupLogin();
        assertTrue(loginPage.isNewUserSignupVisible());
        SignupPage signupPage = loginPage.startSignup("Ada Lovelace", "ada@example.com");
        assertTrue(signupPage.isAccountInformationVisible());
        signupPage.selectTitle(SignupPage.Title.MRS).enterPassword("secure-password");

        assertTrue(actions.contains("navigate:" + HomePage.URL));
        assertTrue(actions.contains("click:By.linkText: Signup / Login"));
        assertTrue(actions.contains("type:Ada Lovelace"));
        assertTrue(actions.contains("type:ada@example.com"));
        assertTrue(actions.contains("click:By.id: id_gender2"));
    }

    @Test
    void accountResultPagesVerifyConfirmationAndContinue() {
        List<String> actions = new ArrayList<>();
        WebDriver driver = createDriver(actions);
        AccountCreatedPage createdPage = new AccountCreatedPage(driver);
        AccountDeletedPage deletedPage = new AccountDeletedPage(driver);

        assertTrue(createdPage.isAccountCreatedVisible());
        createdPage.continueToHome();
        assertTrue(deletedPage.isAccountDeletedVisible());
        deletedPage.continueToHome();

        assertEquals(2, actions.stream()
                .filter(action -> action.equals("click:By.cssSelector: a[data-qa='continue-button']"))
                .count());
    }

    private WebDriver createDriver(List<String> actions) {
        return proxy(WebDriver.class, (proxy, method, arguments) -> {
            if (method.getName().equals("get")) {
                actions.add("navigate:" + arguments[0]);
                return null;
            }
            if (method.getName().equals("findElement")) {
                return createElement((By) arguments[0], actions);
            }
            return null;
        });
    }

    private WebElement createElement(By locator, List<String> actions) {
        return proxy(WebElement.class, (proxy, method, arguments) -> {
            return switch (method.getName()) {
                case "isDisplayed", "isEnabled" -> true;
                case "getText" -> textFor(locator);
                case "click" -> {
                    actions.add("click:" + locator);
                    yield null;
                }
                case "clear" -> null;
                case "sendKeys" -> {
                    actions.add("type:" + String.join("", (CharSequence[]) arguments[0]));
                    yield null;
                }
                case "findElement" -> createElement((By) arguments[0], actions);
                default -> null;
            };
        });
    }

    private String textFor(By locator) {
        String locatorDescription = locator.toString();
        if (locatorDescription.contains("signup-form h2")) {
            return "New User Signup!";
        }
        if (locatorDescription.contains("Enter Account Information")) {
            return "Enter Account Information";
        }
        if (locatorDescription.contains("account-created")) {
            return "Account Created!";
        }
        if (locatorDescription.contains("account-deleted")) {
            return "Account Deleted!";
        }
        return "";
    }

    private <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler));
    }
}