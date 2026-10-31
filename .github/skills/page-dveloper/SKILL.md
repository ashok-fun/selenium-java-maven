---
name: page-dveloper
description: "Create or update Selenium Java page objects from direct user requests delegated by the orchestrator. Use for inspecting pages, choosing stable locators, adding UI interactions, and focused tests."
---

# Page Dveloper Workflow

Use this skill when implementing the page-object portion of a direct user request delegated by the orchestrator.

## Procedure

1. Read the original user request and any clarifications, `.github/instructions/page-dvelopemet.instructions.md`, the target page class, its base/helper classes, and relevant tests.
2. Confirm the page URL, target class path, and requested controls. Users need not provide actions: infer the expected interaction from each control's element type and page purpose. Ask the orchestrator for missing page/control details or genuinely ambiguous behavior, not routine action choices.
3. Inspect the live page with an available browser tool, preferably Microsoft Edge/Playwright when available. Record the observed semantic role and stable DOM attributes for each requested control.
4. If browser inspection is unavailable, use only reliable supplied markup or existing project evidence. If that is not enough to select stable locators, stop and return the blocker to the orchestrator.
5. Add locators and interaction methods using the repository's established page-object helpers and naming conventions. Only implement controls and actions requested or clearly implied by the user's request and clarifications.
6. Add/update focused tests that verify locator selection and interactions without submitting forms or otherwise changing live-site state.
7. Run the smallest relevant test/build command. Check the diff to ensure unrelated edits were preserved.
8. Return changed files, locator evidence, browser-inspection status, test command/result, and unresolved limitations to the orchestrator for independent verification.

## Quality bar

- Stable locators are unique within the intended page region and avoid brittle positional matching.
- Interaction methods are named for their user-visible action and use existing wait/logging helpers.
- Tests cover each requested interaction and preserve pre-existing page behavior.
- The verifier, not this skill, owns independent sign-off. The orchestrator owns all repair-cycle decisions and user-facing summaries.
