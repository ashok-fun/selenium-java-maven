# Selenium Java Maven

A Java and Selenium WebDriver automation framework for browser-based UI testing. The repository includes reusable page objects and UI components, shared browser/test infrastructure, JUnit 5 unit tests, and TestNG UI suite profiles for the AutomationExercise practice site.

## Requirements

- JDK 25
- Apache Maven
- A supported browser for UI suites: Chrome, Firefox, or Microsoft Edge

Confirm the active Java and Maven installations before building:

```shell
java -version
mvn -version
```

## Build and test

Compile the production and test sources:

```shell
mvn clean test-compile
```

Run the default Maven test lifecycle, including the browser-independent unit tests:

```shell
mvn clean test
```

TestNG UI suites are available through Maven profiles:

```shell
mvn -Psmoke test
mvn -Pregression test
mvn -Pcrossbrowser test
```

The smoke and regression profiles run their respective TestNG groups from `src/test/resources/testng/`. The cross-browser profile runs the `crossbrowser` group in parallel on Chrome, Firefox, and Edge.

These UI suites target the external AutomationExercise site. Review the selected test methods before running them: UI tests can create or delete site accounts and otherwise change external state. A code change or compile check does not require running a live UI suite.

## Configuration

Configuration resources are in `src/main/resources/`:

- `config.properties` provides base settings and defaults.
- `env.dev.properties` and `env.qa.properties` provide environment-specific settings.

`ConfigManager` selects the environment from the `environment` system property or base configuration; it defaults to `local`. Environment-specific properties override base properties, and Java system properties override file values. For example:

```shell
mvn -Psmoke test -Denvironment=qa -Dbrowser=firefox
```

Supported browser names are `chrome`, `firefox`, and `edge`; the default is `chrome`. The cross-browser suite supplies a browser parameter for each TestNG run. Configuration includes `baseUrl`, `browser`, wait and page-load timeout settings, and retry count. Set an individual property with `-Dproperty=value` when needed.

## Reports and logs

- Extent Reports are written under `target/extent-reports/` by default, with consolidated and per-test HTML reports.
- Override the report output using `-Dextent.report.path=...` or `-Dextent.report.directory=...`.
- Log4j writes to the console and `logs/automation.log` by default. Override the log directory with `-Dlog.dir=...`.
- Failed UI tests attempt to capture a screenshot and page source in the reports.

Generated build output, reports, and logs should not be committed.

## Project layout

```text
src/
  main/
    java/
      core/         Shared driver, page, wait, and test infrastructure
      config/       Environment and property loading
      reporting/    Extent Reports integration
      ui/
        components/ Reusable UI components
        pages/      Selenium page objects
      utils/        Data and file utilities
    resources/      Application configuration and logging settings
  test/
    java/
      unit/         Browser-independent unit tests
      ui/tests/     TestNG browser-based scenarios
    resources/
      testng/       Smoke, regression, and cross-browser suites
docs/               Test-case specifications
.github/            Copilot instructions, agents, skills, prompts, and workflows
```

## Automation development

- Keep locators and browser interactions in page objects/components; use their public methods from test scenarios.
- Follow the repository's [Copilot instructions](.github/copilot-instructions.md) and [agent workflow](AGENTS.md) when using the project-specific Copilot harness.
- Page-object and TestNG test-script conventions are documented in `.github/instructions/`.
- Preserve unrelated changes and use focused validation before running browser suites.
