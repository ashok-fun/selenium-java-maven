package unit.reporting;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aventstack.extentreports.ExtentTest;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
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
}