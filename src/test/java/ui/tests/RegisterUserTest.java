package ui.tests;

import java.time.LocalDate;
import java.util.UUID;
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
    @Test(groups = {"ui", "regression"})
    public void registerUserAndDeleteAccount() {
        SoftAssert softly = new SoftAssert();
        String userName = "Automation User " + UUID.randomUUID().toString().substring(0, 8);
        String email = "automation." + UUID.randomUUID().toString().replace("-", "") + "@example.com";

        logStep("Open AutomationExercise and verify the home page");
        HomePage homePage = new HomePage(DriverManager.getDriver()).open();
        softly.assertTrue(homePage.isHomePageVisible(), "Home page should be visible");

        logStep("Open Signup / Login and verify the new-user signup form");
        LoginPage loginPage = homePage.clickSignupLogin();
        softly.assertTrue(loginPage.isNewUserSignupVisible(), "New User Signup! should be visible");

        logStep("Submit the initial name and unique email");
        SignupPage signupPage = loginPage.startSignup(userName, email);
        softly.assertTrue(
                signupPage.isAccountInformationVisible(),
                "Enter Account Information should be visible");

        logStep("Fill account information and date of birth");
        signupPage.selectTitle(SignupPage.Title.MR)
                .enterPassword("Selenium-Register-2026!")
                .enterDateOfBirth(LocalDate.of(1990, 5, 15))
                .setNewsletterSubscribed(true)
                .setSpecialOffersEnabled(true)
                .fillAddress(new SignupPage.AddressDetails(
                        "Automation",
                        "User",
                        "Example Company",
                        "123 Test Street",
                        "Suite 4",
                        "United States",
                        "California",
                        "Los Angeles",
                        "90001",
                        "5551234567"));

        logStep("Create the account and verify the confirmation");
        AccountCreatedPage accountCreatedPage = signupPage.createAccount();
        softly.assertTrue(accountCreatedPage.isAccountCreatedVisible(), "Account Created! should be visible");

        logStep("Continue and verify the user is logged in");
        HomePage loggedInHomePage = accountCreatedPage.continueToHome();
        softly.assertTrue(loggedInHomePage.isLoggedInAs(userName), "The new user should be logged in");

        logStep("Delete the account and verify the deletion confirmation");
        AccountDeletedPage accountDeletedPage = loggedInHomePage.deleteAccount();
        softly.assertTrue(accountDeletedPage.isAccountDeletedVisible(), "Account Deleted! should be visible");
        accountDeletedPage.continueToHome();

        softly.assertAll();
    }
}