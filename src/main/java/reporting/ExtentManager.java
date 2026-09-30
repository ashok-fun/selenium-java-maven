package reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import core.FrameworkException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExtentManager {
    private static final String DEFAULT_REPORT_PATH = "target/extent-reports/extent.html";
    private static volatile ExtentReports instance;

    private ExtentManager() {
    }

    public static ExtentReports getInstance() {
        ExtentReports reports = instance;
        if (reports == null) {
            synchronized (ExtentManager.class) {
                reports = instance;
                if (reports == null) {
                    reports = createReports();
                    instance = reports;
                }
            }
        }
        return reports;
    }

    public static void flush() {
        ExtentReports reports = instance;
        if (reports != null) {
            reports.flush();
        }
    }

    private static ExtentReports createReports() {
        Path reportPath = Path.of(System.getProperty("extent.report.path", DEFAULT_REPORT_PATH));
        Path reportDirectory = reportPath.getParent();
        if (reportDirectory != null) {
            try {
                Files.createDirectories(reportDirectory);
            } catch (IOException exception) {
                throw new FrameworkException("Unable to create Extent Reports directory", exception);
            }
        }

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(new ExtentSparkReporter(reportPath.toString()));
        reports.setSystemInfo("OS", System.getProperty("os.name"));
        reports.setSystemInfo("Browser", System.getProperty("browser", "chrome"));
        reports.setSystemInfo("Environment", System.getProperty("environment", "local"));
        reports.setSystemInfo("Base URL", System.getProperty("baseUrl", "not configured"));
        return reports;
    }
}