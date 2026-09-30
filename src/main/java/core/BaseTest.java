package core;

import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import reporting.ExtentLogger;
import reporting.ExtentManager;

public abstract class BaseTest {
    private static final Logger LOGGER = LogManager.getLogger(BaseTest.class);
    private static final String DEFAULT_BROWSER = "chrome";
    private static final ThreadLocal<WebDriver> CURRENT_DRIVER = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    protected void setUp(Method testMethod, ITestContext testContext) {
        ExtentLogger.startTest(testMethod);

        String browser = testContext.getCurrentXmlTest().getParameter("browser");
        if (browser == null || browser.isBlank()) {
            browser = System.getProperty("browser", DEFAULT_BROWSER);
        }
        try {
            WebDriver driver = DriverManager.getDriver(browser);
            CURRENT_DRIVER.set(driver);
            ExtentLogger.info("Started WebDriver for browser: " + browser);
            LOGGER.info("Started WebDriver for test {} on thread {}", testMethod.getName(),
                    Thread.currentThread().getName());
        } catch (RuntimeException exception) {
            ExtentLogger.fail(exception);
            LOGGER.error("WebDriver setup failed for test {}", testMethod.getName(), exception);
            throw exception;
        }
    }

    @AfterMethod(alwaysRun = true)
    protected void tearDown(ITestResult result) {
        try {
            ExtentLogger.logResult(result, CURRENT_DRIVER.get());
        } finally {
            try {
                DriverManager.quitDriver();
            } catch (RuntimeException cleanupFailure) {
                LOGGER.error("WebDriver cleanup failed", cleanupFailure);
                ExtentLogger.logTeardownFailure(cleanupFailure, result);
            } finally {
                CURRENT_DRIVER.remove();
                ExtentLogger.clear();
            }
        }
    }

    @AfterSuite(alwaysRun = true)
    protected void flushReports() {
        ExtentManager.flush();
    }

    protected final void logStep(String message) {
        ExtentLogger.info(message);
    }
}