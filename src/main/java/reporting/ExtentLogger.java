package reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import java.lang.reflect.Method;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.Test;

public final class ExtentLogger {
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private ExtentLogger() {
    }

    public static ExtentTest startTest(Method testMethod) {
        String testName = testMethod.getDeclaringClass().getSimpleName() + "." + testMethod.getName();
        ExtentTest test = ExtentManager.getInstance().createTest(testName);
        Test testAnnotation = testMethod.getAnnotation(Test.class);
        if (testAnnotation != null && testAnnotation.groups().length > 0) {
            test.assignCategory(testAnnotation.groups());
        }
        CURRENT_TEST.set(test);
        return test;
    }

    public static ExtentTest currentTest() {
        return CURRENT_TEST.get();
    }

    public static void info(String message) {
        requireCurrentTest().info(message);
    }

    public static void fail(Throwable failure) {
        requireCurrentTest().fail(failure);
    }

    public static void logResult(ITestResult result, WebDriver driver) {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) {
            return;
        }

        if (result.getStatus() == ITestResult.FAILURE) {
            captureFailureArtifacts(test, driver);
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

    public static void logTeardownFailure(Throwable failure, ITestResult result) {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) {
            return;
        }
        test.warning("WebDriver teardown failed: " + failure.getMessage());
        if (result.getStatus() != ITestResult.FAILURE) {
            result.setStatus(ITestResult.FAILURE);
            result.setThrowable(failure);
            test.fail(failure);
        }
    }

    public static void clear() {
        CURRENT_TEST.remove();
    }

    private static ExtentTest requireCurrentTest() {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) {
            throw new IllegalStateException("No active Extent test is associated with this thread");
        }
        return test;
    }

    private static void captureFailureArtifacts(ExtentTest test, WebDriver driver) {
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
}