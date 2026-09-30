package core;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.ITestResult;
import org.testng.IRetryAnalyzer;

public final class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(RetryAnalyzer.class.getName());
    private static final String RETRY_COUNT_PROPERTY = "test.retry.count";
    private final int maximumRetries;
    private int retryCount;

    public RetryAnalyzer() {
        this(readConfiguredRetryCount());
    }

    public RetryAnalyzer(int maximumRetries) {
        if (maximumRetries < 0) {
            throw new ConfigurationException("test.retry.count must not be negative");
        }
        this.maximumRetries = maximumRetries;
    }

    @Override
    public synchronized boolean retry(ITestResult result) {
        Objects.requireNonNull(result, "result must not be null");
        if (retryCount >= maximumRetries) {
            return false;
        }
        retryCount++;
        LOGGER.log(Level.WARNING, "Retrying test {0} ({1}/{2})", new Object[] {
            result.getName(), retryCount, maximumRetries
        });
        return true;
    }

    public synchronized int getRetryCount() {
        return retryCount;
    }

    private static int readConfiguredRetryCount() {
        String configuredValue = System.getProperty(RETRY_COUNT_PROPERTY, "1");
        try {
            return Integer.parseInt(configuredValue);
        } catch (NumberFormatException exception) {
            throw new ConfigurationException(
                    "System property '" + RETRY_COUNT_PROPERTY + "' must be an integer", exception);
        }
    }
}