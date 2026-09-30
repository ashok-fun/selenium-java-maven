## 1. Common Components for Framework

### Done
- [x] Thread-local `DriverManager` with Chrome, Firefox, and Edge creation through Selenium Manager.
- [x] `BaseTest` lifecycle, per-test Extent nodes, test categories, failure screenshots/page source, step logging, and report flushing.
- [x] `BasePage` and `BaseComponent` with explicit waits, common actions, and root-scoped component lookup.
- [x] Dedicated `FluentWaitUtils` with configurable polling, visible/clickable conditions, and framework timeout wrapping.
- [x] Configurable TestNG `RetryAnalyzer` (`test.retry.count`) and framework exception types for configuration and wait failures.
- [x] Seven reusable UI components: header, footer, sidebar, modal, table, dropdown, and toast.
- [x] `ConfigManager` with file/classpath loading, environment overlays, system-property overrides, typed getters, and base URL selection.
- [x] `EncryptedConfigPlaceholder` extension interface; no encryption implementation.
- [x] JSON, CSV, and Excel readers (`JsonUtils`, `CsvUtils`, `ExcelUtils`), Datafaker-backed random data, download validation, and browser console-log collection.
- [x] Maven dependencies are version-pinned; Surefire and TestNG dependencies are present.

### Pending
- [ ] Extract reporting into the planned thread-safe `ExtentManager` and `ExtentLogger`; current report lifecycle is implemented inside `BaseTest`.
- [ ] Replace `java.util.logging` usage with Log4j2 and add console/rolling-file configuration and `log4j2.xml`.
- [ ] Add `EnvironmentLoader` and bundled `config.properties`, `env.qa.properties`, and `env.dev.properties` resources.
- [ ] Add Checkstyle configuration/plugin and document SonarLint usage.
- [ ] Expand README beyond its current title, add architecture diagram and `CONTRIBUTING.md`.
- [ ] Add Azure DevOps pipeline for build/tests and report artifact publishing.

## 2. UI

### Done
- [x] AutomationExercise page objects cover home, signup/login, account information, account-created, and account-deleted states.
- [x] TestNG Register User flow covers registration, login verification, account deletion, and confirmation with soft assertions and Extent step logging.
- [x] Browser-free unit tests cover UI components and page-object navigation.

### Pending
- [ ] Add `smoke.xml`, `regression.xml`, and `crossbrowser.xml` suites and wire them into Maven/Surefire execution.
- [ ] Move registration inputs into a JSON test-data fixture; current test values are inline (email is unique per run).
- [ ] Execute the flow on a real browser/site and verify selectors, browser behavior, and cleanup. The live registration test has only been compiled, not run, to avoid creating a real account during local validation.
- [ ] Validate Chrome, Firefox, and Edge runs on the target Windows environment; cross-browser driver support exists, but suite execution is not wired.

## 3. API

### Done
- None. No API-specific implementation is present yet.

### Pending
- [ ] Add the pinned RestAssured dependency and API client with base URI, common headers, optional Bearer/OAuth2 token, and request/response logging.
- [ ] Add the Products List GET request builder with query-parameter support.
- [ ] Add response validation for status, required fields, and JSON schema; provide the schema resource.
- [ ] Add the TestNG Products List API test and Extent reporting integration.
- [ ] Add `api.xml` suite and wire API suite execution into Maven/CI.
