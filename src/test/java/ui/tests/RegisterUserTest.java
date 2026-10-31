package ui.tests;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;
import net.datafaker.Faker;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import core.BaseTest;
import core.DriverManager;
import ui.pages.AccountCreatedPage;
import ui.pages.AccountDeletedPage;
import ui.pages.HomePage;
import ui.pages.LoginPage;
import ui.pages.SignupPage;

public class RegisterUserTest extends BaseTest {

    @Test(groups = {"ui", "smoke", "regression", "crossbrowser"})
    public void TC001_registerUser() {
        Faker faker = new Faker(Locale.US);
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String name = firstName + " " + lastName;
        String generatedEmail = faker.internet().safeEmailAddress();
        int emailSeparator = generatedEmail.indexOf('@');
        String email = generatedEmail.substring(0, emailSeparator) + "."
                + UUID.randomUUID().toString().replace("-", "") + generatedEmail.substring(emailSeparator);
        SignupPage.Title title = faker.options().option(SignupPage.Title.class);
        String password = faker.internet().password(12, 20, true, true, true);
        LocalDate dateOfBirth = faker.date().birthdayLocalDate(18, 65);
        String company = faker.company().name();
        String streetAddress = faker.address().streetAddress();
        String address2 = faker.address().secondaryAddress();
        String state = faker.address().state();
        String city = faker.address().city();
        String zipcode = faker.address().zipCode();
        String mobileNumber = faker.phoneNumber().cellPhone();

        logStep("Open AutomationExercise and verify the home page");
        HomePage homePage = new HomePage(DriverManager.getDriver()).open();
        assertTrueWithScreenshot(homePage.isHomePageVisible(), "Home page should be visible");

        logStep("Open Signup / Login and verify the new-user signup form");
        LoginPage loginPage = homePage.clickSignupLogin();
        assertTrueWithScreenshot(loginPage.isNewUserSignupVisible(), "New User Signup! should be visible");

        logStep("Enter a unique name and email and submit signup");
        SignupPage signupPage = loginPage.startSignup(name, email);
        assertTrueWithScreenshot(signupPage.isAccountInformationVisible(),
                "Enter Account Information should be visible");

        logStep("Fill account information and signup preferences");
        assertEqualsWithScreenshot(signupPage.getEmail(), email,
                "Signup email should be carried into account details");
        signupPage.selectTitle(title)
                .enterName(name)
                .enterPassword(password)
                .enterDateOfBirth(dateOfBirth)
                .setNewsletterSubscribed(true)
                .setSpecialOffersEnabled(true);

        logStep("Fill address details and create the account");
        SignupPage.AddressDetails address = new SignupPage.AddressDetails(
                firstName, lastName, company, streetAddress, address2, "United States",
                state, city, zipcode, mobileNumber);
        AccountCreatedPage accountCreatedPage = signupPage.fillAddress(address).createAccount();

        logStep("Verify the account-created confirmation");
        assertTrueWithScreenshot(accountCreatedPage.isAccountCreatedVisible(),
                "ACCOUNT CREATED! should be visible");
    }

    @Test(groups = {"ui", "smoke", "regression", "crossbrowser"})
    public void TC002_registerUserAndDeleteAccount() {
        SoftAssert softly = new SoftAssert();
        String userName = "Automation User " + UUID.randomUUID().toString().substring(0, 8);
        String email = "automation." + UUID.randomUUID().toString().replace("-", "") + "@example.com";

        logStep("Open AutomationExercise and verify the home page");
        HomePage homePage = new HomePage(DriverManager.getDriver()).open();
        assertTrueWithScreenshot(softly, homePage.isHomePageVisible(), "Home page should be visible");

        logStep("Open Signup / Login and verify the new-user signup form");
        LoginPage loginPage = homePage.clickSignupLogin();
        assertTrueWithScreenshot(softly, loginPage.isNewUserSignupVisible(), "New User Signup! should be visible");

        

        softly.assertAll();
    }
}