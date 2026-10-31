# Repository Guidelines

## Project structure

- This is a Java 25 Maven project using Selenium WebDriver.
- Keep shared browser lifecycle, waits, and framework behavior in `src/main/java/core/`.
- Keep reusable UI interactions in `src/main/java/ui/pages/` and `src/main/java/ui/components/`; tests should use their public APIs.
- Configuration, utilities, and reporting belong in their existing packages. Follow established package boundaries rather than introducing parallel frameworks.

## Java and implementation practices

- Follow the surrounding code's naming, formatting, and error-handling conventions. Prefer small, focused changes that preserve existing public behavior.
- Reuse existing helpers such as `BasePage` waits and interactions instead of duplicating them or adding arbitrary sleeps.
- Keep secrets and personal data out of source, tests, logs, and reports. Use synthetic test data; do not commit generated credentials or local configuration.
- Preserve unrelated worktree changes. Review the diff before finishing and do not revert changes outside the task.

## UI automation and tests

- Use page objects for locators and browser interactions; keep test methods focused on the scenario and its assertions.
- Do not run live, state-changing UI tests against external sites unless the user explicitly requests and approves execution.
- Run the smallest relevant validation first. Repository build commands are `mvn clean test-compile` and `mvn clean test`; UI suite profiles include `smoke`, `regression`, and `crossbrowser`. Confirm the scope before running suites that launch browsers or modify external state.
- Report the exact validation command and result. Distinguish compilation or browser-free tests from live browser execution.

## Focused guidance

- For Selenium page-object changes, follow [page-object development instructions](./instructions/page-dvelopemet.instructions.md).
- For Selenium TestNG UI test scripts, follow [test-script development instructions](./instructions/test-script-development.instructions.md).
- The project prerequisites and build commands are also documented in [README.md](../README.md).
