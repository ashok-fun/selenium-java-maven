## 1. Common Components for Framework

### Done

- [X] Thread-local `DriverManager` with Chrome, Firefox, and Edge creation through Selenium Manager.
- [X] `BaseTest` lifecycle, per-test Extent nodes, test categories, failure screenshots/page source, step logging, and report flushing.
- [X] `BasePage` and `BaseComponent` with explicit waits, common actions, and root-scoped component lookup.
- [X] Dedicated `FluentWaitUtils` with configurable polling, visible/clickable conditions, and framework timeout wrapping.
- [X] Configurable TestNG `RetryAnalyzer` (`test.retry.count`) and framework exception types for configuration and wait failures.
- [X] Seven reusable UI components: header, footer, sidebar, modal, table, dropdown, and toast.
- [X] `ConfigManager` with file/classpath loading, environment overlays, system-property overrides, typed getters, and base URL selection.
- [X] `EncryptedConfigPlaceholder` extension interface; no encryption implementation.
- [X] JSON, CSV, and Excel readers (`JsonUtils`, `CsvUtils`, `ExcelUtils`), Datafaker-backed random data, download validation, and browser console-log collection.
- [X] Maven dependencies are version-pinned; Surefire and TestNG dependencies are present.

### Pending

- [ ] Extract reporting into the planned thread-safe `ExtentManager` and `ExtentLogger`; current report lifecycle is implemented inside `BaseTest`.
- [X] Replace framework logging with Log4j2; add console and time/size-based rolling-file appenders in `log4j2.xml`.
- [ ] Add `EnvironmentLoader` and bundled `config.properties`, `env.qa.properties`, and `env.dev.properties` resources.
- [ ] Add Checkstyle configuration/plugin and document SonarLint usage.
- [ ] Expand README beyond its current title, add architecture diagram and `CONTRIBUTING.md`.
- [ ] Add Azure DevOps pipeline for build/tests and report artifact publishing.

## 2. UI

### Done

- [X] AutomationExercise page objects cover home, signup/login, account information, account-created, and account-deleted states.
- [X] TestNG Register User flow covers registration, login verification, account deletion, and confirmation with soft assertions and Extent step logging.
- [X] Browser-free unit tests cover UI components and page-object navigation.

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
