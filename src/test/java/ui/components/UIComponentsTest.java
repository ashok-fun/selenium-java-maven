package ui.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class UIComponentsTest {
    @Test
    void navigationAndModalActionsStayInsideTheirComponentRoots() {
        List<String> actions = new ArrayList<>();
        By headerRoot = By.cssSelector("header");
        WebDriver headerDriver = createDriver(headerRoot, createActionElement(actions, "Home"));
        new HeaderComponent(headerDriver, headerRoot).clickNavigationLink("Home");

        By modalRoot = By.cssSelector(".dialog");
        WebDriver modalDriver = createDriver(modalRoot, createActionElement(actions, "Close"));
        ModalComponent modal = new ModalComponent(modalDriver, modalRoot);
        assertTrue(modal.isVisible());
        modal.close();

        assertEquals(2, actions.stream().filter("click"::equals).count());
    }

    @Test
    void toastExposesVisibilityAndMessage() {
        By toastRoot = By.cssSelector("[role='alert']");
        WebDriver driver = createDriver(toastRoot, createActionElement(new ArrayList<>(), "Saved"));
        ToastComponent toast = new ToastComponent(driver, toastRoot);

        assertTrue(toast.isVisible());
        assertEquals("Saved", toast.getMessage());
    }

    @Test
    void tableReadsRowsAndCellsAndRejectsOutOfRangeIndexes() {
        By tableRoot = By.cssSelector("table");
        WebElement table = createTableElement();
        TableComponent component = new TableComponent(createDriver(tableRoot, table), tableRoot);

        assertEquals(1, component.getRowCount());
        assertEquals("Ada", component.getCellText(0, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> component.getCellText(1, 0));
    }

    @Test
    void dropdownSelectsByVisibleText() {
        By selectRoot = By.cssSelector("select");
        AtomicReference<String> selected = new AtomicReference<>("Chrome");
        WebElement select = createSelectElement(selected);
        DropdownComponent component = new DropdownComponent(createDriver(selectRoot, select), selectRoot);

        component.selectByVisibleText("Firefox");

        assertEquals("Firefox", component.getSelectedOptionText());
    }

    private WebDriver createDriver(By rootLocator, WebElement root) {
        return proxy(WebDriver.class, (proxy, method, arguments) -> {
            if (method.getName().equals("findElement")) {
                return root;
            }
            if (method.getName().equals("findElements") && arguments[0].equals(rootLocator)) {
                return List.of(root);
            }
            return null;
        });
    }

    private WebElement createActionElement(List<String> actions, String text) {
        WebElement target = proxy(WebElement.class, (proxy, method, arguments) -> {
            return switch (method.getName()) {
                case "isDisplayed", "isEnabled" -> true;
                case "click" -> {
                    actions.add("click");
                    yield null;
                }
                case "getText" -> text;
                case "findElement" -> createActionElement(actions, text);
                default -> null;
            };
        });
        return proxy(WebElement.class, (proxy, method, arguments) -> {
            if (method.getName().equals("findElement")) {
                return target;
            }
            if (method.getName().equals("isDisplayed")) {
                return true;
            }
            if (method.getName().equals("getText")) {
                return text;
            }
            return null;
        });
    }

    private WebElement createTableElement() {
        WebElement cell = proxy(WebElement.class, (proxy, method, arguments) ->
                method.getName().equals("getText") ? "Ada" : null);
        WebElement row = proxy(WebElement.class, (proxy, method, arguments) -> {
            if (method.getName().equals("findElements")) {
                return List.of(cell);
            }
            return null;
        });
        return proxy(WebElement.class, (proxy, method, arguments) -> {
            if (method.getName().equals("isDisplayed")) {
                return true;
            }
            if (method.getName().equals("findElements")) {
                return List.of(row);
            }
            return null;
        });
    }

    private WebElement createSelectElement(AtomicReference<String> selected) {
        List<String> optionTexts = List.of("Chrome", "Firefox");
        List<WebElement> options = optionTexts.stream()
                .map(optionText -> proxy(WebElement.class, (proxy, method, arguments) -> {
                    return switch (method.getName()) {
                        case "getText" -> optionText;
                        case "isEnabled" -> true;
                        case "isSelected" -> optionText.equals(selected.get());
                        case "click" -> {
                            selected.set(optionText);
                            yield null;
                        }
                        default -> null;
                    };
                }))
                .toList();
        return proxy(WebElement.class, (proxy, method, arguments) -> {
            return switch (method.getName()) {
                case "getTagName" -> "select";
                case "getAttribute" -> null;
                case "isDisplayed", "isEnabled" -> true;
                case "findElements" -> {
                    String locator = arguments[0].toString();
                    yield optionTexts.stream()
                        .filter(optionText -> !locator.contains("normalize-space")
                            || locator.contains(optionText))
                        .map(optionText -> options.get(optionTexts.indexOf(optionText)))
                        .toList();
                }
                default -> null;
            };
        });
    }

    private <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler));
    }
}