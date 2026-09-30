package unit.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.ConfigurationException;
import core.FluentWaitUtils;
import core.RetryAnalyzer;
import core.WaitTimeoutException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TimeoutException;
import org.testng.ITestResult;

class FluentWaitAndRetryAnalyzerTest {
	@Test
	void fluentWaitReturnsWhenConditionIsSatisfied() {
		AtomicInteger attempts = new AtomicInteger();
		FluentWaitUtils wait = new FluentWaitUtils(
				createDriver(), Duration.ofMillis(200), Duration.ofMillis(5));

		String result = wait.until(driver -> attempts.incrementAndGet() >= 2 ? "ready" : null);

		assertEquals("ready", result);
		assertTrue(attempts.get() >= 2);
	}

	@Test
	void fluentWaitWrapsSeleniumTimeoutWithFrameworkException() {
		FluentWaitUtils wait = new FluentWaitUtils(
				createDriver(), Duration.ofMillis(30), Duration.ofMillis(5));

		WaitTimeoutException exception = assertThrows(
				WaitTimeoutException.class,
				() -> wait.until(driver -> null));

		assertInstanceOf(TimeoutException.class, exception.getCause());
	}

	@Test
	void retryAnalyzerStopsAtConfiguredLimit() {
		RetryAnalyzer retryAnalyzer = new RetryAnalyzer(2);
		ITestResult result = createTestResult();

		assertTrue(retryAnalyzer.retry(result));
		assertTrue(retryAnalyzer.retry(result));
		assertFalse(retryAnalyzer.retry(result));
		assertEquals(2, retryAnalyzer.getRetryCount());
	}

	@Test
	void retryAnalyzerRejectsNegativeRetryLimit() {
		assertThrows(ConfigurationException.class, () -> new RetryAnalyzer(-1));
	}

	private WebDriver createDriver() {
		return proxy(WebDriver.class, (proxy, method, arguments) -> null);
	}

	private ITestResult createTestResult() {
		return proxy(ITestResult.class, (proxy, method, arguments) ->
				method.getName().equals("getName") ? "sampleTest" : null);
	}

	private <T> T proxy(Class<T> type, InvocationHandler handler) {
		return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler));
	}
}
