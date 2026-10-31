package reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Media;
import java.lang.reflect.Method;
import java.nio.file.Path;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.Test;

public final class ExtentLogger {
    private static final ThreadLocal<ActiveTest> CURRENT_TEST = new ThreadLocal<>();

    private ExtentLogger() {
    }

    public static ExtentTest startTest(Method testMethod, String browser) {
        String testName = testMethod.getDeclaringClass().getSimpleName() + "." + testMethod.getName();
        ExtentTest consolidatedTest = ExtentManager.getInstance().createTest(testName);
        ExtentManager.IndividualReport individualReport = ExtentManager.createIndividualReport(
                testMethod.getDeclaringClass().getSimpleName(), testMethod.getName(), browser);
        ExtentTest individualTest = individualReport.reports().createTest(testName);
        Test testAnnotation = testMethod.getAnnotation(Test.class);
        if (testAnnotation != null && testAnnotation.groups().length > 0) {
            consolidatedTest.assignCategory(testAnnotation.groups());
            individualTest.assignCategory(testAnnotation.groups());
        }
        CURRENT_TEST.set(new ActiveTest(consolidatedTest, individualReport, individualTest));
        return consolidatedTest;
    }

    public static ExtentTest currentTest() {
        ActiveTest activeTest = CURRENT_TEST.get();
        return activeTest == null ? null : activeTest.consolidatedTest();
    }

    public static Path currentIndividualReportPath() {
        ActiveTest activeTest = CURRENT_TEST.get();
        return activeTest == null ? null : activeTest.individualReport().path();
    }

    public static void info(String message) {
        ActiveTest activeTest = requireCurrentTest();
        activeTest.consolidatedTest().info(message);
        activeTest.individualTest().info(message);
    }

    public static void warning(String message) {
        ActiveTest activeTest = requireCurrentTest();
        activeTest.consolidatedTest().warning(message);
        activeTest.individualTest().warning(message);
    }

    public static void fail(Throwable failure) {
        ActiveTest activeTest = requireCurrentTest();
        activeTest.consolidatedTest().fail(failure);
        activeTest.individualTest().fail(failure);
    }

    public static void logAssertion(WebDriver driver, boolean passed, String details) {
        ActiveTest activeTest = requireCurrentTest();
        Media screenshot = null;
        try {
            String screenshotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            screenshot = MediaEntityBuilder.createScreenCaptureFromBase64String(
                    screenshotData, "Assertion screenshot").build();
        } catch (WebDriverException | ClassCastException exception) {
            warnBoth(activeTest, "Unable to capture assertion screenshot: " + exception.getMessage());
        }
        Status status = passed ? Status.PASS : Status.FAIL;
        log(activeTest.consolidatedTest(), status, details, screenshot);
        log(activeTest.individualTest(), status, details, screenshot);
    }

    public static void logResult(ITestResult result, WebDriver driver) {
        ActiveTest activeTest = CURRENT_TEST.get();
        if (activeTest == null) {
            return;
        }

        if (result.getStatus() == ITestResult.FAILURE) {
            Throwable failure = result.getThrowable();
            Media screenshot = createScreenshot(driver, activeTest);
            if (failure != null) {
                logFailure(activeTest.consolidatedTest(), failure, screenshot);
                logFailure(activeTest.individualTest(), failure, screenshot);
            } else {
                log(activeTest.consolidatedTest(), Status.FAIL,
                        "Test failed without an exception detail", screenshot);
                log(activeTest.individualTest(), Status.FAIL,
                        "Test failed without an exception detail", screenshot);
            }
            captureFailurePageSource(activeTest, driver);
        } else if (result.getStatus() == ITestResult.SKIP) {
            activeTest.consolidatedTest().log(Status.SKIP, "Test skipped");
            activeTest.individualTest().log(Status.SKIP, "Test skipped");
        } else {
            activeTest.consolidatedTest().pass("Test passed");
            activeTest.individualTest().pass("Test passed");
        }
    }

    public static void logTeardownFailure(Throwable failure, ITestResult result) {
        ActiveTest activeTest = CURRENT_TEST.get();
        if (activeTest == null) {
            return;
        }
        String warning = "WebDriver teardown failed: " + failure.getMessage();
        activeTest.consolidatedTest().warning(warning);
        activeTest.individualTest().warning(warning);
        if (result.getStatus() != ITestResult.FAILURE) {
            result.setStatus(ITestResult.FAILURE);
            result.setThrowable(failure);
            activeTest.consolidatedTest().fail(failure);
            activeTest.individualTest().fail(failure);
        }
    }

    public static void flushCurrentIndividualReport() {
        ActiveTest activeTest = CURRENT_TEST.get();
        if (activeTest != null) {
            activeTest.individualReport().reports().flush();
        }
    }

    public static void clear() {
        CURRENT_TEST.remove();
    }

    private static ActiveTest requireCurrentTest() {
        ActiveTest activeTest = CURRENT_TEST.get();
        if (activeTest == null) {
            throw new IllegalStateException("No active Extent test is associated with this thread");
        }
        return activeTest;
    }

    private static Media createScreenshot(WebDriver driver, ActiveTest activeTest) {
        if (driver == null) {
            return null;
        }
        try {
            String screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            return MediaEntityBuilder.createScreenCaptureFromBase64String(screenshot, "Failure screenshot").build();
        } catch (WebDriverException | ClassCastException exception) {
            warnBoth(activeTest, "Unable to capture failure screenshot: " + exception.getMessage());
            return null;
        }
    }

    private static void captureFailurePageSource(ActiveTest activeTest, WebDriver driver) {
        if (driver == null) {
            return;
        }
        try {
            var pageSource = MarkupHelper.createCodeBlock(driver.getPageSource(), CodeLanguage.XML);
            activeTest.consolidatedTest().info(pageSource);
            activeTest.individualTest().info(pageSource);
        } catch (WebDriverException exception) {
            warnBoth(activeTest, "Unable to capture page source: " + exception.getMessage());
        }
    }

    private static void log(ExtentTest test, Status status, String details, Media media) {
        if (media == null) {
            test.log(status, details);
        } else {
            test.log(status, details, media);
        }
    }

    private static void logFailure(ExtentTest test, Throwable failure, Media media) {
        if (media == null) {
            test.fail(failure);
        } else {
            test.fail(failure, media);
        }
    }

    private static void warnBoth(ActiveTest activeTest, String message) {
        activeTest.consolidatedTest().warning(message);
        activeTest.individualTest().warning(message);
    }

    private record ActiveTest(
            ExtentTest consolidatedTest,
            ExtentManager.IndividualReport individualReport,
            ExtentTest individualTest) {
    }
}