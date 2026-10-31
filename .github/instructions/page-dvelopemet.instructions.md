---
name: Page Object Development
description: "Use when creating or updating Selenium Java page objects, stable UI locators, page interactions, or focused page-object tests."
applyTo: "src/main/java/ui/pages/**/*.java, src/test/java/**/*Page*Test.java"
---

# Selenium Page Object Development

- Inspect the live page or supplied DOM/accessibility evidence before selecting locators. Do not claim browser inspection without using a browser tool.
- Prefer unique stable attributes (`data-*`, `id`, or a stable link `href`) and scope repeated elements to a semantic page region. Avoid generated classes, positional selectors, and brittle absolute XPath.
- Users are expected to identify the page and requested objects, not specify each object's action. Determine the appropriate interaction from the inspected element type and purpose:
  - Links and buttons: add a click method.
  - Text inputs and text areas: add a method to enter the supplied text.
  - Dropdowns/select elements: add a method to select the supplied option/value.
  - Radio buttons and checkboxes: add methods to select or toggle the appropriate option.
  - Other interactive controls: choose the conventional interaction supported by the control and the page's purpose.
- Create a locator for each requested object near the top of the page class and a descriptive method matching its inferred role. Ask for clarification only when the object's purpose or expected behavior cannot reasonably be determined from the page and request.
- Reuse the repository's base-page helpers for waits, interaction, and logging. Preserve existing method behavior and return types.
- Add focused tests that assert locator/action behavior without changing a live site. Follow existing test conventions.
- Run the smallest relevant test/build command. Report the exact command and outcome; distinguish a failed check from one that could not be run.
- Do not invent controls, silently ignore failures, or alter unrelated files.
