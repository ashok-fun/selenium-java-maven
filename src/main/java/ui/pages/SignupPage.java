package ui.pages;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import core.BasePage;
import ui.components.DropdownComponent;

public class SignupPage extends BasePage {
    private static final By ACCOUNT_INFORMATION_HEADING = By.xpath(
            "//h2[.//b[normalize-space()='Enter Account Information']]");
    private static final By PASSWORD = By.id("password");
    private static final By TITLE_MR = By.id("id_gender1");
    private static final By TITLE_MRS = By.id("id_gender2");
    private static final By NEWSLETTER = By.id("newsletter");
    private static final By SPECIAL_OFFERS = By.id("optin");
    private static final By FIRST_NAME = By.id("first_name");
    private static final By LAST_NAME = By.id("last_name");
    private static final By COMPANY = By.id("company");
    private static final By ADDRESS = By.id("address1");
    private static final By ADDRESS_2 = By.id("address2");
    private static final By COUNTRY = By.id("country");
    private static final By STATE = By.id("state");
    private static final By CITY = By.id("city");
    private static final By ZIPCODE = By.id("zipcode");
    private static final By MOBILE_NUMBER = By.id("mobile_number");
    private static final By CREATE_ACCOUNT_BUTTON = By.cssSelector("button[data-qa='create-account']");

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    public boolean isAccountInformationVisible() {
        try {
            return getText(ACCOUNT_INFORMATION_HEADING)
                    .trim()
                    .equalsIgnoreCase("Enter Account Information");
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public SignupPage selectTitle(Title title) {
        Objects.requireNonNull(title, "title must not be null");
        click(title == Title.MR ? TITLE_MR : TITLE_MRS);
        return this;
    }

    public SignupPage enterPassword(String password) {
        type(PASSWORD, password);
        return this;
    }

    public SignupPage enterDateOfBirth(LocalDate dateOfBirth) {
        Objects.requireNonNull(dateOfBirth, "dateOfBirth must not be null");
        new DropdownComponent(driver, By.id("days"))
                .selectByVisibleText(Integer.toString(dateOfBirth.getDayOfMonth()));
        new DropdownComponent(driver, By.id("months"))
                .selectByVisibleText(dateOfBirth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        new DropdownComponent(driver, By.id("years"))
                .selectByVisibleText(Integer.toString(dateOfBirth.getYear()));
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
        click(CREATE_ACCOUNT_BUTTON);
        return new AccountCreatedPage(driver);
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