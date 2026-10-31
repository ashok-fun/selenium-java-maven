package reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import core.FrameworkException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

public final class ExtentManager {
    private static final String DEFAULT_REPORT_PATH = "target/extent-reports/extent.html";
    private static final String SCREENSHOT_COLUMN_SCRIPT = """
            (() => {
              document.querySelectorAll('table.table.table-sm').forEach((table) => {
                const headerRow = table.querySelector('thead tr');
                if (!headerRow || headerRow.querySelector('.screenshot-column')) return;
                const headers = Array.from(headerRow.cells);
                const detailsIndex = headers.findIndex(
                  (header) => header.textContent.trim().toLowerCase() === 'details');
                if (detailsIndex < 0) return;

                const screenshotHeader = document.createElement('th');
                screenshotHeader.className = 'screenshot-column';
                screenshotHeader.textContent = 'Screenshot';
                headerRow.insertBefore(screenshotHeader, headers[detailsIndex + 1] || null);

                table.querySelectorAll('tbody tr.event-row').forEach((row) => {
                  const cells = Array.from(row.cells);
                  const detailsCell = cells[detailsIndex];
                  const screenshotCell = document.createElement('td');
                  screenshotCell.className = 'screenshot-cell';
                  const imageLink = detailsCell && detailsCell.querySelector(
                    'a[data-featherlight="image"][href^="data:image/"]');

                  if (imageLink) {
                    const preview = document.createElement('img');
                    preview.className = 'screenshot-preview';
                    preview.src = imageLink.href;
                    preview.alt = 'Assertion screenshot preview';
                    preview.style.width = '88px';
                    preview.style.maxHeight = '60px';
                    preview.style.objectFit = 'contain';
                    preview.style.cursor = 'zoom-in';
                    imageLink.replaceChildren(preview);
                    imageLink.title = 'Click to view full-size screenshot';
                    screenshotCell.appendChild(imageLink);
                  }

                  row.insertBefore(screenshotCell, cells[detailsIndex + 1] || null);
                });
              });
            })();
            """;
    private static final DateTimeFormatter RUN_TIMESTAMP_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss", Locale.ROOT);
    private static final DateTimeFormatter TEST_TIMESTAMP_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS", Locale.ROOT);
    private static final String RUN_TIMESTAMP = LocalDateTime.now().format(RUN_TIMESTAMP_FORMAT);
    private static final String RUN_FOLDER_NAME = "Consolidated report_" + RUN_TIMESTAMP;
    private static final Path REPORT_ROOT = reportRoot();
    private static final Path RUN_DIRECTORY = REPORT_ROOT.resolve(RUN_FOLDER_NAME);
    private static final Path CONSOLIDATED_REPORT_PATH = REPORT_ROOT.resolve(RUN_FOLDER_NAME + ".html");
    private static final AtomicLong INDIVIDUAL_REPORT_SEQUENCE = new AtomicLong();
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

    public static IndividualReport createIndividualReport(String className, String testName, String browser) {
        getInstance();
        String timestamp = LocalDateTime.now().format(TEST_TIMESTAMP_FORMAT);
        long sequence = INDIVIDUAL_REPORT_SEQUENCE.incrementAndGet();
        String fileName = safeFileName(className) + "_" + safeFileName(testName) + "_"
                + safeFileName(browser) + "_" + timestamp + "_" + sequence + ".html";
        Path reportPath = RUN_DIRECTORY.resolve(fileName);
        ExtentReports reports = new ExtentReports();
        reports.attachReporter(createSparkReporter(reportPath));
        addSystemInfo(reports, browser);
        return new IndividualReport(reports, reportPath);
    }

    public static Path getConsolidatedReportPath() {
        return CONSOLIDATED_REPORT_PATH;
    }

    public static Path getRunDirectory() {
        return RUN_DIRECTORY;
    }

    public record IndividualReport(ExtentReports reports, Path path) {
    }

    private static String safeFileName(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]+", "_");
    }

    private static Path reportRoot() {
        String configuredDirectory = System.getProperty("extent.report.directory");
        if (configuredDirectory != null && !configuredDirectory.isBlank()) {
            return Path.of(configuredDirectory);
        }
        Path configuredReport = Path.of(System.getProperty("extent.report.path", DEFAULT_REPORT_PATH));
        Path parent = configuredReport.getParent();
        return parent == null ? Path.of(".") : parent;
    }

    private static ExtentReports createReports() {
        try {
            Files.createDirectories(REPORT_ROOT);
            Files.createDirectories(RUN_DIRECTORY);
        } catch (IOException exception) {
            throw new FrameworkException("Unable to create Extent Reports directory", exception);
        }

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(createSparkReporter(CONSOLIDATED_REPORT_PATH));
        addSystemInfo(reports, System.getProperty("browser", "chrome"));
        return reports;
    }

    private static ExtentSparkReporter createSparkReporter(Path reportPath) {
        ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath.toString());
        reporter.config().setJs(SCREENSHOT_COLUMN_SCRIPT);
        return reporter;
    }

    private static void addSystemInfo(ExtentReports reports, String browser) {
        reports.setSystemInfo("OS", System.getProperty("os.name"));
        reports.setSystemInfo("Browser", browser);
        reports.setSystemInfo("Environment", System.getProperty("environment", "local"));
        reports.setSystemInfo("Base URL", System.getProperty("baseUrl", "not configured"));
    }
}