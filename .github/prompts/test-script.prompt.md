---
name: Test Script
description: "Implement and independently verify a Selenium Java UI test method from a written test case using existing page objects."
argument-hint: "Provide the test-case path, target Java test file, and exact test method name."
agent: orchestrator
---

Task: Implement a Selenium Java UI test script from the provided test case.

Test case file: `docs\TC001_registerUser.txt`
Target test file: `src\test\java\ui\tests\RegisterUserTest.java`
Test method: `TC001_registerUser`

Read and validate the test case, target file, method, existing page-object APIs, and project TestNG conventions before delegating. The test developer should implement only the specified method using existing page functions. The verifier must independently review the script. Allow at most one developer repair based on review comments, followed by one verifier review, then publish the result and validation summary.

Do not execute the live registration flow unless the user explicitly requests and approves it. For another task, replace the three input values above with the user's exact paths and method name; do not guess if an input is missing or ambiguous.
