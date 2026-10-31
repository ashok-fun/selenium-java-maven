package ui.pages;

import core.BasePage;
import core.WaitTimeoutException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ui.components.DropdownComponent;

public class SignupPage extends BasePage {
    private static final By ACCOUNT_INFORMATION_HEADING = By.xpath(
            "//h2[.//b[normalize-space()='Enter Account Information']]");
    private static final By ADDRESS_INFORMATION_HEADING = By.xpath(
            "//h2[.//b[normalize-space()='Address Information']]");
    private static final By TITLE_MR = By.id("id_gender1");
    private static final By TITLE_MRS = By.id("id_gender2");
    private static final By NAME = By.cssSelector("input[data-qa='name']");
    private static final By EMAIL = By.cssSelector("input[data-qa='email']");
    private static final By PASSWORD = By.cssSelector("input[data-qa='password']");
    private static final By DAYS = By.cssSelector("select[data-qa='days']");
    private static final By MONTHS = By.cssSelector("select[data-qa='months']");
    private static final By YEARS = By.cssSelector("select[data-qa='years']");
    private static final By NEWSLETTER = By.id("newsletter");
    private static final By SPECIAL_OFFERS = By.id("optin");
    private static final By FIRST_NAME = By.cssSelector("input[data-qa='first_name']");
    private static final By LAST_NAME = By.cssSelector("input[data-qa='last_name']");
    private static final By COMPANY = By.cssSelector("input[data-qa='company']");
    private static final By ADDRESS = By.cssSelector("input[data-qa='address']");
    private static final By ADDRESS_2 = By.cssSelector("input[data-qa='address2']");
    private static final By COUNTRY = By.cssSelector("select[data-qa='country']");
    private static final By STATE = By.cssSelector("input[data-qa='state']");
    private static final By CITY = By.cssSelector("input[data-qa='city']");
    private static final By ZIPCODE = By.cssSelector("input[data-qa='zipcode']");
    private static final By MOBILE_NUMBER = By.cssSelector("input[data-qa='mobile_number']");
    private static final By CREATE_ACCOUNT_BUTTON = By.cssSelector("button[data-qa='create-account']");

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    public boolean isAccountInformationVisible() {
        return isHeadingVisible(ACCOUNT_INFORMATION_HEADING, "Enter Account Information");
    }

    public boolean isAddressInformationVisible() {
        return isHeadingVisible(ADDRESS_INFORMATION_HEADING, "Address Information");
    }

    public SignupPage selectTitle(Title title) {
        Objects.requireNonNull(title, "title must not be null");
        click(title == Title.MR ? TITLE_MR : TITLE_MRS);
        return this;
    }

    public SignupPage enterName(String name) {
        type(NAME, name);
        return this;
    }

    public String getEmail() {
        return waitForVisible(EMAIL).getAttribute("value");
    }

    public SignupPage enterPassword(String password) {
        type(PASSWORD, password);
        return this;
    }

    public SignupPage enterDateOfBirth(LocalDate dateOfBirth) {
        Objects.requireNonNull(dateOfBirth, "dateOfBirth must not be null");
        new DropdownComponent(driver, DAYS).selectByVisibleText(Integer.toString(dateOfBirth.getDayOfMonth()));
        new DropdownComponent(driver, MONTHS).selectByVisibleText(
                dateOfBirth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        new DropdownComponent(driver, YEARS).selectByVisibleText(Integer.toString(dateOfBirth.getYear()));
        return this;
    }

    public SignupPage setNewsletterSubscribed(boolean subscribed) {
        setCheckbox(NEWSLETTER, subscribed);
        return this;
    }

    public SignupPage setSpecialOffersEnabled(boolean enabled) {
        setCheckbox(SPECIAL_OFFERS, enabled);
        return this;
    }

    public SignupPage fillAddress(AddressDetails addressDetails) {
        Objects.requireNonNull(addressDetails, "addressDetails must not be null");
        type(FIRST_NAME, addressDetails.firstName());
        type(LAST_NAME, addressDetails.lastName());
        type(COMPANY, addressDetails.company());
        type(ADDRESS, addressDetails.address());
        type(ADDRESS_2, addressDetails.address2());
        new DropdownComponent(driver, COUNTRY).selectByVisibleText(addressDetails.country());
        type(STATE, addressDetails.state());
        type(CITY, addressDetails.city());
        type(ZIPCODE, addressDetails.zipcode());
        type(MOBILE_NUMBER, addressDetails.mobileNumber());
        return this;
    }

    public AccountCreatedPage createAccount() {
        WebElement createAccountButton = waitForClickable(CREATE_ACCOUNT_BUTTON);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'start', inline: 'nearest'});",
                createAccountButton);
        click(CREATE_ACCOUNT_BUTTON);
        return new AccountCreatedPage(driver);
    }

    private boolean isHeadingVisible(By locator, String expectedText) {
        try {
            return getText(locator).trim().equalsIgnoreCase(expectedText);
        } catch (WaitTimeoutException exception) {
            return false;
        }
    }

    private void setCheckbox(By locator, boolean checked) {
        WebElement checkbox = waitForVisible(locator);
        if (checkbox.isSelected() != checked) {
            click(locator);
        }
    }

    public enum Title {
        MR,
        MRS
    }

    public record AddressDetails(
            String firstName,
            String lastName,
            String company,
            String address,
            String address2,
            String country,
            String state,
            String city,
            String zipcode,
            String mobileNumber) {
        public AddressDetails {
            Objects.requireNonNull(firstName, "firstName must not be null");
            Objects.requireNonNull(lastName, "lastName must not be null");
            Objects.requireNonNull(company, "company must not be null");
            Objects.requireNonNull(address, "address must not be null");
            Objects.requireNonNull(address2, "address2 must not be null");
            Objects.requireNonNull(country, "country must not be null");
            Objects.requireNonNull(state, "state must not be null");
            Objects.requireNonNull(city, "city must not be null");
            Objects.requireNonNull(zipcode, "zipcode must not be null");
            Objects.requireNonNull(mobileNumber, "mobileNumber must not be null");
        }
    }
}
