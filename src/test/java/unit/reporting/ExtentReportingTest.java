package unit.reporting;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
        ExtentTest mainThreadTest = ExtentLogger.startTest(testMethod);
        ExtentLogger.info("Main-thread report step");
        AtomicReference<ExtentTest> workerThreadTest = new AtomicReference<>();
        AtomicReference<Throwable> workerFailure = new AtomicReference<>();

        Thread worker = new Thread(() -> {
            try {
                ExtentLogger.startTest(testMethod);
                workerThreadTest.set(ExtentLogger.currentTest());
                ExtentLogger.info("Worker-thread report step");
            } catch (Throwable failure) {
                workerFailure.set(failure);
            } finally {
                ExtentLogger.clear();
            }
        });
        worker.start();
        worker.join();

        assertNull(workerFailure.get());
        assertNotSame(mainThreadTest, workerThreadTest.get());
        assertSame(mainThreadTest, ExtentLogger.currentTest());
        ExtentLogger.clear();
        assertNull(ExtentLogger.currentTest());
    }
}