package ui.components;

import java.util.List;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import core.BaseComponent;

public class TableComponent extends BaseComponent {
    private static final By ROOT = By.cssSelector("table");
    private static final By ROWS = By.cssSelector("tbody tr");
    private static final By CELLS = By.cssSelector("th, td");

    public TableComponent(WebDriver driver) {
        this(driver, ROOT);
    }

    public TableComponent(WebDriver driver, By rootLocator) {
        super(driver, Objects.requireNonNull(rootLocator, "rootLocator must not be null"));
    }

    public int getRowCount() {
        int rowCount = findElements(ROWS).size();
        logger.info("Read table row count: " + rowCount);
        return rowCount;
    }

    public String getCellText(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || columnIndex < 0) {
            throw new IndexOutOfBoundsException("Table row and column indexes must be non-negative");
        }
        List<WebElement> rows = findElements(ROWS);
        if (rowIndex >= rows.size()) {
            throw new IndexOutOfBoundsException("Table row index is out of range: " + rowIndex);
        }
        List<WebElement> cells = rows.get(rowIndex).findElements(CELLS);
        if (columnIndex >= cells.size()) {
            throw new IndexOutOfBoundsException("Table column index is out of range: " + columnIndex);
        }
        String text = cells.get(columnIndex).getText();
        logger.info("Read table cell at row " + rowIndex + ", column " + columnIndex);
        return text;
    }
}