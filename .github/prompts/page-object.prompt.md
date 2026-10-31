---
name: Page Object
description: "Create a Selenium Java page object from a prepared, editable task."
argument-hint: "Describe any updates to the prepared task, or leave blank to use it as written."
agent: orchestrator
---

# Prepared task: Create a Selenium page object

This is a prepared task prompt. You may update its details before invoking it; use the task below as written unless the user provides changes. When the user invokes this prompt with a task, follow the workflow below.

## Task details

- **Task:** Create a Selenium page object
- **Page name:** Home
- **Page URL:** https://automationexercise.com/login
- **Page object file:** `src\main\java\ui\pages\LoginPage.java`
- **Objects:** All objects present on the page except the header and footer.

## Orchestrator workflow

1. First verify the request and all essential inputs. If anything is missing or ambiguous, stop and ask the user before delegating.
2. Verify the page identity before delegation. The page name `Home`, `/login` URL, and `LoginPage.java` filename may be inconsistent. Inspect the available page or other trustworthy evidence; if that does not resolve the identity, stop and ask the user for clarification.
3. Once the task is clear, choose the appropriate subagent. For a standard page-object request, delegate implementation to `page-dveloper`, then send its result to `verifier` for independent verification. Do not implement the page-object code yourself.
4. Retain the existing limit: allow at most one developer repair after verification, followed by at most one additional verifier review. If issues remain, stop and report them to the user.

Keep the requested object scope excluding the header and footer. Follow the [page-development instruction](../instructions/page-dvelopemet.instructions.md) for locator selection, interactions, tests, and evidence; do not duplicate its coding rules here.
