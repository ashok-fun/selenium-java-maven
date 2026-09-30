package utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public final class ExcelUtils {
    private ExcelUtils() {
    }

    public static List<Map<String, String>> read(Path path, String sheetName) throws IOException {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(sheetName, "sheetName must not be null");
        try (Workbook workbook = WorkbookFactory.create(path.toFile())) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Excel sheet not found: " + sheetName);
            }
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null || headerRow.getLastCellNum() < 0) {
                return List.of();
            }

            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            List<String> headers = readCells(headerRow, formatter, evaluator);
            List<Map<String, String>> records = new ArrayList<>();
            for (int rowIndex = sheet.getFirstRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isBlank(row, headers.size(), formatter, evaluator)) {
                    continue;
                }
                List<String> values = readCells(row, headers.size(), formatter, evaluator);
                Map<String, String> record = new LinkedHashMap<>();
                for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                    String header = headers.get(columnIndex);
                    if (!header.isBlank()) {
                        record.put(header, values.get(columnIndex));
                    }
                }
                records.add(record);
            }
            return records;
        }
    }

    private static List<String> readCells(
            Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        return readCells(row, row.getLastCellNum(), formatter, evaluator);
    }

    private static List<String> readCells(
            Row row, int columnCount, DataFormatter formatter, FormulaEvaluator evaluator) {
        List<String> values = new ArrayList<>(columnCount);
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            values.add(cell == null ? "" : formatter.formatCellValue(cell, evaluator).trim());
        }
        return values;
    }

    private static boolean isBlank(
            Row row, int columnCount, DataFormatter formatter, FormulaEvaluator evaluator) {
        return readCells(row, columnCount, formatter, evaluator).stream().allMatch(String::isBlank);
    }
}