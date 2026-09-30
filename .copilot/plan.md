1. [ ] DriverManager (Thread‑Local WebDriver + Cross‑Browser)
    Create a DriverManager class using Java and Selenium that supports thread‑local WebDriver instances for parallel TestNG execution. Include Chrome, Firefox, and Edge support for Windows 11. Use Selenium Manager for driver binaries. Provide methods: getDriver(), setDriver(), quitDriver(). Ensure thread safety and clean teardown. Do not modify other framework files.

2. BaseTest (Setup, Teardown, Extent Reports Integration)
   Generate a BaseTest class that initializes and tears down WebDriver using DriverManager. Integrate Extent Reports: create test nodes, attach screenshots on failure, and log steps. Ensure compatibility with parallel TestNG execution. Do not modify other classes.
3. BasePage + BaseComponent (POM + Component Model)
   Create a BasePage class and a BaseComponent class for a POM + Component Object Model framework. Include WebDriver reference, fluent wait utilities, common actions (click, type, getText), and logging. Ensure both classes are reusable and extendable. Do not modify unrelated files.
4. UI Components (Header, Footer, Sidebar, Modal, Table, Dropdown, Toast)
   Generate component classes: HeaderComponent, FooterComponent, SidebarComponent, ModalComponent, TableComponent, DropdownComponent, ToastComponent. Each should extend BaseComponent and expose reusable actions. Keep locators clean and descriptive. Do not modify other modules.
5. Page Objects for AutomationExercise (Signup/Login + Registration)
   Create Page Object classes for automationexercise.com to support Test Case 1: Register User. Include HomePage, LoginPage, SignupPage, AccountCreatedPage. Use POM + Component Object Model. Include clean locators, reusable actions, and assertions placeholders. Do not modify other framework files.
6. UI Test: Register User (TestNG)
   Generate a TestNG test class for Test Case 1: Register User on automationexercise.com. Use the Page Objects and Components previously created. Integrate Extent Reports logging and soft assertions. Ensure thread-safe execution. Do not modify other modules.
7. API Client (RestAssured)
   Create an APIClient class using RestAssured. Include base URI loading from properties, common headers, OAuth2/Bearer token support, and logging of requests/responses. Do not modify unrelated files.
8. API Request Builder (Products List)
   Create a ProductsListRequestBuilder class that builds a GET request for https://automationexercise.com/api/productsList. Include query params support and reusable builder pattern. Do not modify other modules.
9. API Response Validator (Schema + Assertions)
   Create a ProductsListResponseValidator class that validates the response for the products list API. Include JSON schema validation, status code checks, and key field assertions. Integrate with Extent Reports. Do not modify unrelated files.
10. API Test (TestNG)
    Generate a TestNG test class for the Products List API. Use the APIClient, ProductsListRequestBuilder, and ProductsListResponseValidator. Integrate Extent Reports logging. Do not modify other modules.
11. Utilities (JSON Reader, CSV/Excel Reader, Random Data, File Download, Console Logs)
    Create utility classes: JsonDataReader, CsvReader, ExcelReader, RandomDataGenerator, FileDownloadValidator, BrowserConsoleLogCollector. Each utility should be standalone, reusable, and not modify other framework files.
12. ConfigManager (.properties loader)
    Create a ConfigManager class that loads .properties files and exposes typed getters. Include environment selection logic. Add a placeholder interface for future encrypted config support. Do not modify other modules.
13. Retry Analyzer (Flaky Test Handling)
    Create a RetryAnalyzer class for TestNG that retries failed tests a configurable number of times. Integrate logging. Do not modify other modules.
14. Extent Reports Setup (Singleton)
    Create an ExtentManager class that initializes Extent Reports as a thread-safe singleton. Include environment metadata and report folder creation. Do not modify unrelated files.
15. Documentation (README + Architecture Diagram + Contribution Guide)
    Generate a README.md, ASCII architecture diagram, and CONTRIBUTING.md for this automation framework. Include setup instructions, folder structure, how to add UI/API tests, how parallel execution works, and how reporting/logging is integrated. Do not modify code files.
16. Azure DevOps Pipeline (YAML)
    Create an Azure DevOps YAML pipeline that checks out code, installs Java and Maven,
