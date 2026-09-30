package core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class BasePageTest {
    @Test
    void pageActionsWaitAndDelegateToLocatedElement() {
        List<String> actions = new ArrayList<>();
        TestPage page = new TestPage(createDriver(actions, By.cssSelector("header")));

        page.clickElement(By.id("submit"));
        page.enterText(By.id("email"), "person@example.com");
        String text = page.readText(By.id("message"));

        assertEquals(List.of(
            "find:" + By.id("submit"),
            "click",
            "find:" + By.id("email"),
            "clear",
            "type:person@example.com",
            "find:" + By.id("message")), actions);
        assertEquals("element text", text);
    }

    @Test
    void componentActionsSearchWithinItsRootElement() {
        List<String> actions = new ArrayList<>();
        By rootLocator = By.cssSelector("header");
        WebDriver driver = createDriver(actions, rootLocator);
        TestComponent component = new TestComponent(driver, rootLocator);

        component.clickElement(By.linkText("Account"));

        assertTrue(actions.contains("find:" + rootLocator));
        assertTrue(actions.contains("find:" + By.linkText("Account")));
        assertTrue(actions.contains("click"));
    }

    private WebDriver createDriver(List<String> actions, By rootLocator) {
        WebElement target = createElement(actions);
        WebElement root = (WebElement) Proxy.newProxyInstance(
                WebElement.class.getClassLoader(),
                new Class<?>[] {WebElement.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("findElement")) {
                        actions.add("find:" + arguments[0]);
                        return target;
                    }
                    return elementResult(method.getName(), arguments, actions);
                });
        return (WebDriver) Proxy.newProxyInstance(
                WebDriver.class.getClassLoader(),
                new Class<?>[] {WebDriver.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("findElement")) {
                        actions.add("find:" + arguments[0]);
                        return arguments[0].equals(rootLocator) ? root : target;
                    }
                    return null;
                });
    }

    private WebElement createElement(List<String> actions) {
        return (WebElement) Proxy.newProxyInstance(
                WebElement.class.getClassLoader(),
                new Class<?>[] {WebElement.class},
                (proxy, method, arguments) -> elementResult(method.getName(), arguments, actions));
    }

    private Object elementResult(String methodName, Object[] arguments, List<String> actions) {
        return switch (methodName) {
            case "isDisplayed", "isEnabled" -> true;
            case "click" -> {
                actions.add("click");
                yield null;
            }
            case "clear" -> {
                actions.add("clear");
                yield null;
            }
            case "sendKeys" -> {
                actions.add("type:" + String.join("", (CharSequence[]) arguments[0]));
                yield null;
            }
            case "getText" -> "element text";
            default -> null;
        };
    }

    private static final class TestPage extends BasePage {
        private TestPage(WebDriver driver) {
            super(driver);
        }

        private void clickElement(By locator) {
            click(locator);
        }

        private void enterText(By locator, String text) {
            type(locator, text);
        }

        private String readText(By locator) {
            return getText(locator);
        }
    }

    private static final class TestComponent extends BaseComponent {
        private TestComponent(WebDriver driver, By rootLocator) {
            super(driver, rootLocator);
        }

        private void clickElement(By locator) {
            click(locator);
        }
    }
}