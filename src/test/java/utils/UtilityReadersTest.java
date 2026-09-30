package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.Logs;

class UtilityReadersTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void jsonReaderMapsGenericData() throws Exception {
        Path jsonFile = temporaryDirectory.resolve("user.json");
        Files.writeString(jsonFile, "{\"name\":\"Ada\",\"active\":true}");

        Map<String, Object> user = JsonUtils.read(jsonFile, new TypeReference<>() { });

        assertEquals("Ada", user.get("name"));
        assertEquals(true, user.get("active"));
    }

    @Test
    void csvReaderSupportsQuotedValuesAndHeaders() throws Exception {
        Path csvFile = temporaryDirectory.resolve("users.csv");
        Files.writeString(csvFile, "name,city\n\"Ada Lovelace\",\"London, UK\"\n");

        List<Map<String, String>> rows = CsvUtils.read(csvFile);

        assertEquals(1, rows.size());
        assertEquals("Ada Lovelace", rows.get(0).get("name"));
        assertEquals("London, UK", rows.get(0).get("city"));
    }

    @Test
    void excelReaderReturnsHeaderKeyedCellValues() throws Exception {
        Path workbookFile = temporaryDirectory.resolve("users.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Users");
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("age");
            var row = sheet.createRow(1);
            row.createCell(0).setCellValue("Ada");
            row.createCell(1).setCellValue(36);
            try (OutputStream output = Files.newOutputStream(workbookFile)) {
                workbook.write(output);
            }
        }

        List<Map<String, String>> rows = ExcelUtils.read(workbookFile, "Users");

        assertEquals(1, rows.size());
        assertEquals("Ada", rows.get(0).get("name"));
        assertEquals("36", rows.get(0).get("age"));
    }

    @Test
    void randomDataUsesValidFormatsAndInclusiveBounds() {
        LocalDate boundary = LocalDate.of(2020, 1, 1);

        assertTrue(!RandomDataGenerator.randomFirstName().isBlank());
        assertTrue(!RandomDataGenerator.randomLastName().isBlank());
        assertTrue(RandomDataGenerator.randomEmail().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"));
        assertEquals(boundary, RandomDataGenerator.randomDate(boundary, boundary));
        assertThrows(IllegalArgumentException.class,
                () -> RandomDataGenerator.randomDate(boundary.plusDays(1), boundary));
    }

    @Test
    void downloadValidatorChecksSizeAndExtension() throws Exception {
        Path download = temporaryDirectory.resolve("report.CSV");
        Files.writeString(download, "id,name\n1,Ada\n");

        FileDownloadValidator.DownloadInfo info = FileDownloadValidator.validate(download, 1, ".csv");

        assertEquals("csv", info.extension());
        assertTrue(info.sizeBytes() > 1);
        assertThrows(java.io.IOException.class,
                () -> FileDownloadValidator.validate(download, 1000, "csv"));
        assertThrows(java.io.IOException.class,
                () -> FileDownloadValidator.validate(download, 1, "json"));
    }

    @Test
    void browserConsoleCollectorReturnsLogsAndFiltersSevereMessages() {
        LogEntry severeEntry = new LogEntry(Level.SEVERE, 1L, "Uncaught error");
        LogEntry infoEntry = new LogEntry(Level.INFO, 2L, "Page loaded");
        LogEntries entries = new LogEntries(List.of(severeEntry, infoEntry));
        Logs logs = proxy(Logs.class, (proxy, method, arguments) ->
                method.getName().equals("get") ? entries : null);
        WebDriver.Options options = proxy(WebDriver.Options.class, (proxy, method, arguments) ->
                method.getName().equals("logs") ? logs : null);
        WebDriver driver = proxy(WebDriver.class, (proxy, method, arguments) ->
                method.getName().equals("manage") ? options : null);

        assertEquals(2, BrowserConsoleLogCollector.collect(driver).size());
        assertEquals(List.of("Uncaught error"), BrowserConsoleLogCollector.collectSevereMessages(driver));
    }

    private <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler));
    }
}