---
name: test-script-development
description: 'Create or update Selenium Java TestNG UI test scripts from written test cases. Use when implementing a specified test method with existing page objects, validating step mapping, and preparing safe focused validation.'
---

# Selenium Test Script Development

Use this workflow for a test-script implementation delegated by the orchestrator. Do not create a test script unless the user has asked for implementation; setup/customization requests alone do not authorize test-file edits.

## Procedure

1. Read the original request, `.github/instructions/test-script-development.instructions.md`, the full test-case file, and the exact target test file/method.
2. Confirm the case path, target path, and method name match the orchestrator's validated inputs. Stop and report discrepancies instead of guessing.
3. Read the relevant page objects, `BaseTest`, neighboring tests, and build configuration. Map every case step to an existing public page-object method and every expected result to a meaningful assertion.
4. If a necessary action is not exposed by existing page objects, report the gap to the orchestrator. Do not silently add page-object changes outside the delegated scope.
5. Implement only the requested test method, preserving unrelated worktree changes and surrounding test behavior. Use existing TestNG and test-data conventions.
6. Use synthetic, unique data where necessary. Avoid hard-coded secrets, arbitrary waits, direct UI selectors in the test, and unrequested side effects.
7. Validate safely. Prefer Java test compilation or browser-free focused tests. Do not run a real-site registration or other state-changing workflow unless the user explicitly requested and approved execution.
8. Inspect the final diff for scope, report changed files, map case steps to methods/assertions, and return exact validation evidence and limitations to the orchestrator.

## Quality bar

- Every stated case step is represented or its omission is explained.
- Assertions verify the expected behavior, not merely that the test completed.
- Test implementation follows existing lifecycle, reporting, and cleanup conventions.
- Validation clearly distinguishes compile-only checks from actual scenario execution.
