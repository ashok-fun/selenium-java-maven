package unit.core;

import core.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

class DriverManagerTest {
    @AfterEach
    void cleanUpDriver() {
        DriverManager.quitDriver();
    }

    @Test
    void storesDriverPerThread() throws InterruptedException {
        WebDriver mainThreadDriver = createDriverProxy(new AtomicBoolean(), false);
        WebDriver workerThreadDriver = createDriverProxy(new AtomicBoolean(), false);
        AtomicReference<Throwable> workerFailure = new AtomicReference<>();
        DriverManager.setDriver(mainThreadDriver);

        Thread worker = new Thread(() -> {
            try {
                DriverManager.setDriver(workerThreadDriver);
                assertSame(workerThreadDriver, DriverManager.getDriver());
                DriverManager.quitDriver();
            } catch (Throwable failure) {
                workerFailure.set(failure);
            }
        });
        worker.start();
        worker.join();

        assertNull(workerFailure.get());
        assertSame(mainThreadDriver, DriverManager.getDriver());
    }

    @Test
    void quitDriverQuitsAndRemovesDriverEvenWhenQuitFails() {
        AtomicBoolean quitCalled = new AtomicBoolean();
        DriverManager.setDriver(createDriverProxy(quitCalled, true));

        assertThrows(IllegalStateException.class, DriverManager::quitDriver);
        assertTrue(quitCalled.get());

        String previousBrowser = System.getProperty("browser");
        System.setProperty("browser", "unsupported");
        try {
            assertThrows(IllegalArgumentException.class, DriverManager::getDriver);
        } finally {
            if (previousBrowser == null) {
                System.clearProperty("browser");
            } else {
                System.setProperty("browser", previousBrowser);
            }
        }
    }

    @Test
    void rejectsUnsupportedBrowserNames() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DriverManager.getDriver("safari"));

        assertTrue(exception.getMessage().contains("Supported browsers"));
    }

    private WebDriver createDriverProxy(AtomicBoolean quitCalled, boolean failOnQuit) {
        return (WebDriver) Proxy.newProxyInstance(
                WebDriver.class.getClassLoader(),
                new Class<?>[] {WebDriver.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("quit")) {
                        quitCalled.set(true);
                        if (failOnQuit) {
                            throw new IllegalStateException("Simulated quit failure");
                        }
                    }
                    return null;
                });
    }
}