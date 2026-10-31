---
name: Selenium UI Test Script Development
description: "Use when creating or updating Selenium Java TestNG UI test scripts from written test cases, especially scripts that reuse existing page objects."
applyTo: "src/test/java/ui/tests/**/*.java"
---

# Selenium UI Test Script Development

- Treat the referenced test case as the source of truth. Implement its stated actions and verifications; do not add unrequested behavior.
- Reuse existing page objects and their public methods. Keep selectors, waits, and UI interaction details in page objects rather than duplicating them in test methods.
- Follow the repository's TestNG annotations, `BaseTest` lifecycle, assertion strategy, reporting helpers, naming, and data-generation patterns.
- Keep changes within the named test method and file unless an explicitly approved requirement needs a related change.
- Use unique, synthetic data where the workflow requires uniqueness. Never hard-code real credentials or personal data.
- Assert each stated outcome clearly. Avoid soft assertions unless the test's reporting convention requires them; do not silently continue past a prerequisite failure.
- Avoid arbitrary sleeps, broad catches, ignored failures, and success-shaped fallbacks.
- Do not execute live, state-changing UI flows as part of routine validation. Compile or run browser-free checks unless the user explicitly requests and approves safe live execution.
- Preserve all unrelated changes in the worktree.
