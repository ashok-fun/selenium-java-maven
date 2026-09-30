package core;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public abstract class BaseTest {
    private static final String DEFAULT_BROWSER = "chrome";
    private static final String DEFAULT_REPORT_PATH = "target/extent-reports/extent.html";
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();
    private static final ThreadLocal<WebDriver> CURRENT_DRIVER = new ThreadLocal<>();
    private static volatile ExtentReports extentReports;

    @BeforeMethod(alwaysRun = true)
    protected void setUp(Method testMethod) {
        ExtentTest test = getExtentReports().createTest(
                testMethod.getDeclaringClass().getSimpleName() + "." + testMethod.getName());
        CURRENT_TEST.set(test);

        Test testAnnotation = testMethod.getAnnotation(Test.class);
        if (testAnnotation != null && testAnnotation.groups().length > 0) {
            test.assignCategory(testAnnotation.groups());
        }

        String browser = System.getProperty("browser", DEFAULT_BROWSER);
        try {
            WebDriver driver = DriverManager.getDriver(browser);
            CURRENT_DRIVER.set(driver);
            test.log(Status.INFO, "Started WebDriver for browser: " + browser);
        } catch (RuntimeException exception) {
            test.fail(exception);
            throw exception;
        }
    }

    @AfterMethod(alwaysRun = true)
    protected void tearDown(ITestResult result) {
        ExtentTest test = CURRENT_TEST.get();
        try {
            if (test != null) {
                if (result.getStatus() == ITestResult.FAILURE) {
                    captureFailureArtifacts(test);
                    Throwable failure = result.getThrowable();
                    if (failure != null) {
                        test.fail(failure);
                    } else {
                        test.fail("Test failed without an exception detail");
                    }
                } else if (result.getStatus() == ITestResult.SKIP) {
                    test.log(Status.SKIP, "Test skipped");
                } else {
                    test.pass("Test passed");
                }
            }
        } finally {
            try {
                DriverManager.quitDriver();
            } catch (RuntimeException cleanupFailure) {
                if (test != null) {
                    test.warning("WebDriver teardown failed: " + cleanupFailure.getMessage());
                    if (result.getStatus() != ITestResult.FAILURE) {
                        result.setStatus(ITestResult.FAILURE);
                        result.setThrowable(cleanupFailure);
                        test.fail(cleanupFailure);
                    }
                }
            } finally {
                CURRENT_DRIVER.remove();
                CURRENT_TEST.remove();
            }
        }
    }

    @AfterSuite(alwaysRun = true)
    protected void flushReports() {
        ExtentReports reports = extentReports;
        if (reports != null) {
            reports.flush();
        }
    }

    protected final void logStep(String message) {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) {
            throw new IllegalStateException("No active Extent test is associated with this thread");
        }
        test.info(message);
    }

    private void captureFailureArtifacts(ExtentTest test) {
        WebDriver driver = CURRENT_DRIVER.get();
        if (driver == null) {
            return;
        }

        try {
            String screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            test.addScreenCaptureFromBase64String(screenshot, "Failure screenshot");
        } catch (WebDriverException | ClassCastException exception) {
            test.warning("Unable to capture failure screenshot: " + exception.getMessage());
        }

        try {
            test.info(MarkupHelper.createCodeBlock(driver.getPageSource(), CodeLanguage.XML));
        } catch (WebDriverException exception) {
            test.warning("Unable to capture page source: " + exception.getMessage());
        }
    }

    private static ExtentReports getExtentReports() {
        ExtentReports reports = extentReports;
        if (reports == null) {
            synchronized (BaseTest.class) {
                reports = extentReports;
                if (reports == null) {
                    reports = createExtentReports();
                    extentReports = reports;
                }
            }
        }
        return reports;
    }

    private static ExtentReports createExtentReports() {
        Path reportPath = Path.of(System.getProperty("extent.report.path", DEFAULT_REPORT_PATH));
        Path reportDirectory = reportPath.getParent();
        if (reportDirectory != null) {
            try {
                Files.createDirectories(reportDirectory);
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to create Extent Reports directory", exception);
            }
        }

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(new ExtentSparkReporter(reportPath.toString()));
        reports.setSystemInfo("OS", System.getProperty("os.name"));
        reports.setSystemInfo("Browser", System.getProperty("browser", DEFAULT_BROWSER));
        reports.setSystemInfo("Environment", System.getProperty("environment", "local"));
        reports.setSystemInfo("Base URL", System.getProperty("baseUrl", "not configured"));
        return reports;
    }
}