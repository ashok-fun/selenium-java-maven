You are an expert Java automation architect.
Generate an **enterprise‑ready skeleton automation framework** for **web UI + API testing** with the following requirements:

GOAL

- Create a **single‑module Maven project** that implements:
  - **Selenium WebDriver** for UI tests
  - **RestAssured** for API tests
  - **TestNG** as the test runner
  - **Extent Reports** for reporting
  - **Log4j2** for logging
  - **JSON** for test data
  - **.properties** files for environment configuration
- Use **Page Object Model (POM) + Component Object Model** for UI.
- Make the framework **enterprise‑ready, scalable, and maintainable**.

LANGUAGE / BUILD

- Language: **Java 11+**
- Build tool: **Maven**
- Enforce **strict version locking** in `pom.xml` (no version ranges).
- Add plugins for:
  - **Surefire** (TestNG)
  - **Checkstyle**
- Mention **SonarLint** usage in documentation (no config needed in code).

PACKAGE & PROJECT STRUCTURE

```
src/main/java/
    core/
        DriverManager.java
        BaseTest.java
        FluentWaitUtils.java
        RetryAnalyzer.java
    ui/
        pages/
        components/
    api/
        client/
        requests/
        validators/
    config/
        ConfigManager.java
        EnvironmentLoader.java
        EncryptedConfigPlaceholder.java
    reporting/
        ExtentManager.java
        ExtentLogger.java
    logging/
        Log4j2 configuration classes
    utils/
        JsonDataReader.java
        CsvReader.java
        ExcelReader.java
        RandomDataGenerator.java
        FileDownloadValidator.java
        BrowserConsoleLogCollector.java

src/test/java/
    ui/tests/
        RegisterUserTest.java
    api/tests/
        ProductsListApiTest.java

src/main/resources/
    config.properties
    env.qa.properties
    env.dev.properties
    log4j2.xml
    api-schema/
        products-list-schema.json

src/test/resources/
    testdata/
        user-testdata.json
    testng/
        smoke.xml
        regression.xml
        crossbrowser.xml
        api.xml

Project package root:
    (no prefix — use simple package names like core, ui.pages, api.client, etc.)
```

SELENIUM / DRIVER MANAGEMENT

- Implement a **DriverManager** that:
  - Uses **thread‑local WebDriver** to support **parallel execution** on local machine.
  - Supports **Chrome, Firefox, Edge** on **Windows 11**.
  - Uses **WebDriverManager** (if needed) or clearly documented local driver path strategy.
- Provide:
  - A `BaseTest` class that:
    - Initializes and tears down WebDriver per test.
    - Integrates with Extent Reports.
    - Handles parallel execution safely.

PARALLEL EXECUTION / TESTNG

- Configure **TestNG** for parallel execution using suites:
  - `smoke.xml`
  - `regression.xml`
  - `crossbrowser.xml`
  - `api.xml`
- Ensure:
  - Thread‑safe WebDriver usage.
  - Proper reporting per thread/test.

PAGE OBJECT MODEL + COMPONENT OBJECT MODEL

- Implement:
  - **Page classes** for main screens.
  - **Component classes** for:
    - Header
    - Footer
    - Sidebar
    - Modal
    - Table
    - Dropdown
    - Toast notifications
- Use:
  - Clear separation of responsibilities.
  - Reusable component methods.
  - Fluent, readable actions.

API TESTING (RestAssured)

- Use a **separate API client + request builder + response validator pattern**:
  - API client: base configuration, base URL, common headers.
  - Request builders: encapsulate request creation.
  - Response validators: reusable assertions and schema validation.
- Include:
  - **Automatic schema validation** (e.g., JSON schema files).
  - Built‑in support for **OAuth2 / Bearer tokens** (config‑driven).
  - Integration of **API test results into Extent Reports**.

REPORTING (Extent Reports)

- Integrate **Extent Reports** with:
  - **Screenshots on failure**.
  - **Page source capture on failure**.
  - **Test categorization** (e.g., smoke, regression, api, crossbrowser).
  - **Environment metadata** (OS, browser, base URL, environment name).
- No custom themes required—use standard theme.

LOGGING (Log4j2)

- Configure **Log4j2**:
  - Console + rolling file appenders.
  - Different log levels (INFO, DEBUG, ERROR).
- Add logging to:
  - Driver initialization and teardown.
  - Page actions and components.
  - API requests and responses.
  - Error handling and utilities.

UTILITIES & COMMON MODULESCreate utilities for:

- **JSON test data reader**.
- **Excel/CSV readers** (even though JSON is primary).
- **Random data generator** (names, emails, dates).
- **File download validator** (verify file exists, size, type).
- **Browser console log collector** (capture logs for failed tests).
- **Retry logic** for flaky tests (TestNG retry analyzer).
- **Fluent waits** (custom wait utility).
- **Custom exceptions** for framework‑specific errors.

CONFIGURATION

- Environment configuration via `.properties` files:
  - `config.properties` (base URL, browser, environment).
  - `env.qa.properties`, `env.dev.properties`, etc. (if needed).
- Provide a **ConfigManager** class to:
  - Load properties.
  - Expose typed getters.
- For **config encryption**, only:
  - Add a **placeholder interface** or TODO comment indicating where enterprise teams can plug in their own secrets/encryption solution (no actual encryption implementation).

ERROR HANDLING & RECOVERY

- Implement:
  - Automatic **screenshot + page source capture on failure**.
  - **Soft assertions support** (e.g., using TestNG soft asserts or a custom wrapper).
- Do **not** implement automatic browser restart on driver crash.

SAMPLE TESTSCreate sample tests:

1. **UI Test**

   - Use site: `https://www.automationexercise.com/test_cases`
   - Implement **Test Case 1: Register User** as a full UI flow:
     - Navigate to site.
     - Click “Signup / Login”.
     - Fill registration form.
     - Submit and validate successful registration.
   - Use POM + Component Object Model:
     - Pages and components for header, forms, etc.
     - Assertions integrated with Extent Reports and soft assertions.
2. **API Test**

   - API: **Get All Products List**
     - URL: `https://automationexercise.com/api/productsList`
   - Implement:
     - API client configuration.
     - Request builder for GET products list.
     - Response validator:
       - Status code.
       - Basic schema validation.
       - Key fields presence.
   - Report results via Extent Reports.

CODE QUALITY

- Add **Checkstyle** configuration (e.g., Google style or a reasonable default).
- Mention **SonarLint** in README as recommended IDE plugin.

DOCUMENTATIONGenerate:

1. **README.md**

   - Overview of the framework.
   - Tech stack (Selenium, RestAssured, TestNG, Extent Reports, Log4j2, Maven).
   - How to:
     - Configure environment.
     - Run smoke, regression, crossbrowser, and api suites.
     - Add new pages, components, and API tests.
   - Parallel execution explanation.
   - Extent Reports usage and location of reports.
   - Logging and utilities overview.
2. **Architecture diagram (ASCII)**

   - Show high‑level modules:
     - Core
     - UI (pages + components + tests)
     - API (client + requests + validators + tests)
     - Config
     - Reporting
     - Logging
     - Utils
3. **Contribution guidelines**

   - Branching strategy.
   - Code style expectations.
   - How to add new tests/pages/components.
   - How to extend API clients and validators.

CI/CD (Azure DevOps)

- Provide a sample **Azure DevOps YAML pipeline file** that:
  - Checks out code.
  - Sets up Java and Maven.
  - Runs:
    - Smoke suite.
    - Regression suite (optional or separate stage).
    - API suite.
  - Publishes:
    - TestNG results.
    - Extent Reports artifacts.

OUTPUT EXPECTATION

- Generate:
  - `pom.xml` with dependencies and plugins.
  - Full **folder structure** and key classes/interfaces.
  - Example implementations for:
    - DriverManager
    - BaseTest
    - ConfigManager
    - Reporting setup
    - Logging setup
    - Utilities
    - Sample UI and API tests
  - TestNG suite XML files.
  - README, ASCII architecture diagram, contribution guidelines.
  - Azure DevOps YAML pipeline file.

Make the code **clean, well‑commented, and ready for enterprise extension**.
