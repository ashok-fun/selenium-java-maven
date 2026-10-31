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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class AutomationExercisePagesTest {
    @Test
    void loginPageExposesLoginControlsAndSubmitsEnteredCredentials() {
        List<String> actions = new ArrayList<>();
        LoginPage loginPage = new LoginPage(createDriver(actions));

        assertTrue(loginPage.isLoginFormVisible());
        assertTrue(loginPage.isLoginSignupSeparatorVisible());
        loginPage.login("ada@example.com", "secure-password");

        assertTrue(actions.contains("type:ada@example.com"));
        assertTrue(actions.contains("type:secure-password"));
        assertTrue(actions.contains("click:" + By.cssSelector("button[data-qa='login-button']")));
    }

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
        assertTrue(actions.contains("click:" + By.linkText("Signup / Login")));
        assertTrue(actions.contains("type:Ada Lovelace"));
        assertTrue(actions.contains("type:ada@example.com"));
        assertTrue(actions.contains("click:By.id: id_gender2"));
    }

    @Test
    void signupPageHandlesAccountInformationAndPreferences() {
        List<String> actions = new ArrayList<>();
        SignupPage signupPage = new SignupPage(createDriver(actions));

        assertTrue(signupPage.isAccountInformationVisible());
        assertTrue(signupPage.isAddressInformationVisible());
        assertEquals("ada@example.com", signupPage.getEmail());
        signupPage.selectTitle(SignupPage.Title.MRS)
                .enterName("Ada Lovelace")
                .enterPassword("secure-password")
                .enterDateOfBirth(LocalDate.of(1985, 2, 14))
                .setNewsletterSubscribed(true)
                .setSpecialOffersEnabled(true);

        assertTrue(actions.contains("type:Ada Lovelace"));
        assertTrue(actions.contains("type:secure-password"));
        assertTrue(actions.contains("click:" + By.id("id_gender2")));
        assertTrue(actions.contains("click:" + By.id("newsletter")));
        assertTrue(actions.contains("click:" + By.id("optin")));
        assertTrue(actions.contains("option-click:14"));
        assertTrue(actions.contains("option-click:February"));
        assertTrue(actions.contains("option-click:1985"));
    }

    @Test
    void signupPageFillsAddressAndCreatesAccount() {
        List<String> actions = new ArrayList<>();
        SignupPage signupPage = new SignupPage(createDriver(actions));
        SignupPage.AddressDetails address = new SignupPage.AddressDetails(
                "Ada", "Lovelace", "Analytical Engines", "12 St James Square",
                "Flat 2", "United States", "New York", "New York", "10001", "5551234567");

        AccountCreatedPage result = signupPage.fillAddress(address).createAccount();

        assertTrue(result instanceof AccountCreatedPage);
        assertTrue(actions.contains("type:Ada"));
        assertTrue(actions.contains("type:Lovelace"));
        assertTrue(actions.contains("type:Analytical Engines"));
        assertTrue(actions.contains("type:12 St James Square"));
        assertTrue(actions.contains("type:Flat 2"));
        assertTrue(actions.contains("type:New York"));
        assertTrue(actions.contains("type:10001"));
        assertTrue(actions.contains("type:5551234567"));
        assertTrue(actions.contains("option-click:United States"));
        assertTrue(actions.contains("click:" + By.cssSelector("button[data-qa='create-account']")));
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
                case "getTagName" -> locator.toString().contains("select") ? "select" : "input";
                case "getAttribute" -> locator.toString().contains("data-qa='email'")
                        && "value".equals(arguments[0]) ? "ada@example.com" : null;
                case "click" -> {
                    actions.add("click:" + locator);
                    yield null;
                }
                case "clear" -> null;
                case "sendKeys" -> {
                    actions.add("type:" + String.join("", (CharSequence[]) arguments[0]));
                    yield null;
                }
                case "isSelected" -> false;
                case "findElements" -> locator.toString().contains("select")
                        ? optionsFor(locator, actions)
                        : List.of();
                case "findElement" -> createElement((By) arguments[0], actions);
                default -> null;
            };
        });
    }

    private List<WebElement> optionsFor(By locator, List<String> actions) {
        List<String> optionTexts = switch (locator.toString()) {
            case "By.cssSelector: select[data-qa='days']" -> List.of("14");
            case "By.cssSelector: select[data-qa='months']" -> List.of("February");
            case "By.cssSelector: select[data-qa='years']" -> List.of("1985");
            case "By.cssSelector: select[data-qa='country']" -> List.of("United States");
            default -> List.of();
        };
        return optionTexts.stream().map(text -> proxy(WebElement.class, (proxy, method, arguments) -> {
            return switch (method.getName()) {
                case "getText" -> text;
                case "isSelected" -> false;
                case "isDisplayed", "isEnabled" -> true;
                case "click" -> {
                    actions.add("option-click:" + text);
                    yield null;
                }
                default -> null;
            };
        })).toList();
    }

    private String textFor(By locator) {
        String locatorDescription = locator.toString();
        if (locatorDescription.contains(".login-form h2")) {
            return "Login to your account";
        }
        if (locatorDescription.contains("normalize-space()='OR'")) {
            return "OR";
        }
        if (locatorDescription.contains("signup-form h2")) {
            return "New User Signup!";
        }
        if (locatorDescription.contains("Enter Account Information")) {
            return "Enter Account Information";
        }
        if (locatorDescription.contains("Address Information")) {
            return "Address Information";
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