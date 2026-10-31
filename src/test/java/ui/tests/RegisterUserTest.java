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
    @Test(groups = {"ui", "smoke", "regression", "crossbrowser"})
    public void TC001_registerUser() {
        
    }

    @Test(groups = {"ui", "smoke", "regression", "crossbrowser"})
    public void TC002_registerUserAndDeleteAccount() {
        SoftAssert softly = new SoftAssert();
        String userName = "Automation User " + UUID.randomUUID().toString().substring(0, 8);
        String email = "automation." + UUID.randomUUID().toString().replace("-", "") + "@example.com";

        logStep("Open AutomationExercise and verify the home page");
        HomePage homePage = new HomePage(DriverManager.getDriver()).open();
        softly.assertTrue(homePage.isHomePageVisible(), "Home page should be visible");

        logStep("Open Signup / Login and verify the new-user signup form");
        LoginPage loginPage = homePage.clickSignupLogin();
        softly.assertTrue(loginPage.isNewUserSignupVisible(), "New User Signup! should be visible");

        

        softly.assertAll();
    }
}