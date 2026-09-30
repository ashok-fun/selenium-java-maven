package core;

import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.IRetryAnalyzer;

public final class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger LOGGER = LogManager.getLogger(RetryAnalyzer.class);
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
        LOGGER.warn("Retrying test {} ({}/{})", result.getName(), retryCount, maximumRetries);
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