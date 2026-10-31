package unit.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aventstack.extentreports.ExtentTest;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import reporting.ExtentLogger;
import reporting.ExtentManager;

class ExtentReportingTest {
    @Test
    void managerIsSingletonAndLoggerNodesAreThreadLocal() throws Exception {
        assertSame(ExtentManager.getInstance(), ExtentManager.getInstance());
        Method testMethod = getClass().getDeclaredMethod("managerIsSingletonAndLoggerNodesAreThreadLocal");
        ExtentTest mainThreadTest = ExtentLogger.startTest(testMethod, "chrome");
        String reportPath = ExtentLogger.currentIndividualReportPath().toString();
        ExtentLogger.info("Main-thread report step");
        AtomicReference<ExtentTest> workerThreadTest = new AtomicReference<>();
        AtomicReference<String> workerReportPath = new AtomicReference<>();
        AtomicReference<Throwable> workerFailure = new AtomicReference<>();

        Thread worker = new Thread(() -> {
            try {
                ExtentLogger.startTest(testMethod, "edge");
                workerThreadTest.set(ExtentLogger.currentTest());
                workerReportPath.set(ExtentLogger.currentIndividualReportPath().toString());
                ExtentLogger.info("Worker-thread report step");
            } catch (Throwable failure) {
                workerFailure.set(failure);
            } finally {
                ExtentLogger.flushCurrentIndividualReport();
                ExtentLogger.clear();
            }
        });
        worker.start();
        worker.join();

        assertNull(workerFailure.get());
        assertTrue(reportPath.contains(
            "ExtentReportingTest_managerIsSingletonAndLoggerNodesAreThreadLocal_chrome_"));
        assertTrue(workerReportPath.get().contains("_edge_"));
        assertNotSame(mainThreadTest, workerThreadTest.get());
        assertSame(mainThreadTest, ExtentLogger.currentTest());
        ExtentLogger.flushCurrentIndividualReport();
        ExtentLogger.clear();
        assertNull(ExtentLogger.currentTest());
    }

    @Test
    void screenshotsAreEmbeddedInBothReportFiles() throws Exception {
        Method testMethod = getClass().getDeclaredMethod("screenshotsAreEmbeddedInBothReportFiles");
        ExtentLogger.startTest(testMethod, "chrome");
        String individualReportPath = ExtentLogger.currentIndividualReportPath().toString();
        String consolidatedReportPath = ExtentManager.getConsolidatedReportPath().toString();
        WebDriver driver = (WebDriver) Proxy.newProxyInstance(
                WebDriver.class.getClassLoader(),
                new Class<?>[] {WebDriver.class, TakesScreenshot.class},
                (proxy, method, arguments) -> method.getName().equals("getScreenshotAs")
                        ? "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/Jx8AAAAASUVORK5CYII="
                        : null);

        try {
            ExtentLogger.logAssertion(driver, true, "Passing assertion with screenshot");
            ExtentLogger.logAssertion(driver, false, "Failing assertion with screenshot");
            ExtentLogger.flushCurrentIndividualReport();
            ExtentManager.flush();

            String embeddedImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/Jx8AAAAASUVORK5CYII=";
            for (String reportPath : List.of(individualReportPath, consolidatedReportPath)) {
                String report = Files.readString(Path.of(reportPath));
                assertEventContains(report, "Passing assertion with screenshot", "pass-bg", embeddedImage);
                assertEventContains(report, "Failing assertion with screenshot", "fail-bg", embeddedImage);
                assertEquals(2, report.split("<tr class=\"event-row\">", -1).length - 1);
                assertTrue(report.contains("screenshot-column"));
                assertTrue(report.contains("screenshot-preview"));
                assertTrue(report.contains("Click to view full-size screenshot"));
            }
        } finally {
            ExtentLogger.clear();
        }
    }

    private void assertEventContains(String report, String details, String status, String image) {
        int detailsIndex = report.indexOf(details);
        assertTrue(detailsIndex >= 0);
        int eventStart = report.lastIndexOf("<tr class=\"event-row\">", detailsIndex);
        int eventEnd = report.indexOf("</tr>", detailsIndex);
        assertTrue(eventStart >= 0 && eventEnd > detailsIndex);
        String event = report.substring(eventStart, eventEnd);
        assertTrue(event.contains(status));
        assertTrue(event.contains(image));
    }
}