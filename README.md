# Automation Panda Test Automation Framework

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Selenium](https://img.shields.io/badge/Selenium-4.39.0-green.svg)](https://www.selenium.dev/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.33.0-green.svg)](https://cucumber.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.11.0-blue.svg)](https://testng.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9.0-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> Enterprise-grade Selenium Cucumber BDD test automation framework for web application testing with comprehensive reporting, parallel execution, and robust error handling.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Reporting](#reporting)
- [Best Practices](#best-practices)
- [CI/CD Integration](#cicd-integration)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## 🎯 Overview

The **Automation Panda Framework** is a comprehensive, enterprise-ready test automation solution built for testing the Automation Panda blog website. This framework demonstrates industry best practices in test automation, combining Behavior-Driven Development (BDD) with robust Page Object Model implementation.

### Key Features

- ✅ **BDD with Cucumber**: Human-readable test scenarios using Gherkin syntax
- ✅ **Page Object Model**: Maintainable and reusable page abstractions
- ✅ **Parallel Execution**: Concurrent test execution for faster feedback
- ✅ **Comprehensive Reporting**: Multiple report formats (HTML, JSON, XML)
- ✅ **Cross-browser Testing**: Support for multiple browsers via WebDriverManager
- ✅ **Advanced Waiting**: Both WebDriverWait and FluentWait implementations
- ✅ **Detailed Logging**: Contextual logging with SLF4J for debugging
- ✅ **Configuration Management**: Externalized test configuration
- ✅ **Exception Handling**: Robust error handling and recovery mechanisms

## 🏗️ Architecture

### Design Patterns

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Feature Files │───▶│   Step Definitions │───▶│   Page Objects  │
│   (.feature)    │    │   (HomePageSteps) │    │   (BasePage)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Test Execution  │◀───│   Test Runner   │◀───│   Configuration │
│   (TestNG)      │    │   (TestRunner)  │    │   (config.prop)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Core Components

1. **Page Object Model**: Abstract web pages into reusable components
2. **Step Definitions**: Bridge between Gherkin scenarios and page objects
3. **Test Runner**: Configures and executes Cucumber scenarios
4. **Configuration**: Externalized settings for different environments
5. **Reporting**: Multiple output formats for different stakeholders

## 🛠️ Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Language** | Java | 17 | Core programming language |
| **Testing Framework** | TestNG | 7.11.0 | Test execution and parallelization |
| **BDD Framework** | Cucumber | 7.33.0 | Behavior-driven development |
| **Browser Automation** | Selenium WebDriver | 4.39.0 | Web browser automation |
| **Browser Management** | WebDriverManager | 6.3.3 | Automatic driver management |
| **Build Tool** | Maven | 3.9.0 | Dependency management and build |
| **Logging** | SLF4J + Simple | 2.0.9 | Structured logging |
| **Assertions** | JUnit Jupiter | 5.10.1 | Assertion library |

## 📋 Prerequisites

### System Requirements

- **Java**: JDK 17 or higher
- **Maven**: 3.6.0 or higher
- **Git**: For version control
- **Internet Connection**: For dependency downloads and browser drivers

### Browser Support

- ✅ Chrome (recommended)
- ✅ Firefox
- ✅ Edge
- ✅ Safari (macOS only)

### Environment Setup

```bash
# Verify Java installation
java -version
# Expected: Java 17+

# Verify Maven installation
mvn -version
# Expected: Apache Maven 3.6.0+

# Clone the repository
git clone <repository-url>
cd AutomationPanda
```

## 🚀 Quick Start

### 1. Clone and Setup

```bash
git clone <repository-url>
cd AutomationPanda
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Run Tests

```bash
# Run all tests
mvn test

# Run specific feature
mvn test -Dcucumber.filter.tags="@smoke"

# Run with specific browser
mvn test -Dbrowser=chrome
```

### 4. View Reports

Reports are generated in `target/cucumber-reports/`:
- **HTML Report**: `cucumber.html`
- **JSON Report**: `cucumber.json`
- **TestNG XML**: `cucumber-testng.xml`

## 📁 Project Structure

```
AutomationPanda/
├── src/
│   ├── main/
│   │   └── java/                 # Main source (if any)
│   └── test/
│       ├── java/
│       │   ├── features/         # Cucumber feature files
│       │   │   ├── homepage.feature
│       │   │   └── search.feature
│       │   ├── hooks/            # Cucumber hooks
│       │   │   └── CucumberHooks.java
│       │   ├── pages/            # Page Object classes
│       │   │   ├── BasePage.java
│       │   │   ├── HomePage.java
│       │   │   └── SearchResultsPage.java
│       │   ├── runner/           # Test runners
│       │   │   └── TestRunner.java
│       │   ├── steps/            # Step definitions
│       │   │   ├── HomePageSteps.java
│       │   │   └── SearchSteps.java
│       │   └── utils/            # Utility classes
│       │       ├── ConfigReader.java
│       │       ├── DriverManager.java
│       │       └── ScrollIntoView.java
│       └── resources/            # Test resources
│           └── config.properties
├── target/                       # Build artifacts
│   └── cucumber-reports/         # Test reports
├── pom.xml                       # Maven configuration
├── README.md                     # This file
└── .gitignore                    # Git ignore rules
```

## ⚙️ Configuration

### Test Configuration (`src/test/resources/config.properties`)

```properties
# Browser Configuration
browser=chrome
headless=false
browser.timeout=30

# Application URLs
base.url=https://automationpanda.com
homepage.url=https://automationpanda.com/

# Test Data
default.search.term=selenium
test.timeout=10

# Reporting
report.dir=target/cucumber-reports
screenshot.on.failure=true
```

### Maven Profiles

```xml
<!-- Development Profile -->
<profile>
    <id>dev</id>
    <properties>
        <browser>chrome</browser>
        <headless>false</headless>
    </properties>
</profile>

<!-- CI Profile -->
<profile>
    <id>ci</id>
    <properties>
        <browser>chrome</browser>
        <headless>true</headless>
    </properties>
</profile>
```

## 🏃 Running Tests

### Command Line Execution

```bash
# Run all tests
mvn test

# Run with specific profile
mvn test -Pci

# Run specific tags
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@regression"
mvn test -Dcucumber.filter.tags="not @wip"

# Run with custom browser
mvn test -Dbrowser=firefox

# Run in headless mode
mvn test -Dheadless=true

# Run with parallel execution
mvn test -Dparallel=methods -DthreadCount=4
```

### IDE Execution

1. **IntelliJ IDEA**:
   - Right-click on `TestRunner.java`
   - Select "Run TestRunner"

2. **Eclipse**:
   - Right-click on project
   - Run As → Maven Test

### Test Categories

| Tag | Description | Example |
|-----|-------------|---------|
| `@smoke` | Critical functionality tests | `mvn test -Dcucumber.filter.tags="@smoke"` |
| `@regression` | Full regression suite | `mvn test -Dcucumber.filter.tags="@regression"` |
| `@homepage` | Homepage-specific tests | `mvn test -Dcucumber.filter.tags="@homepage"` |
| `@search` | Search functionality tests | `mvn test -Dcucumber.filter.tags="@search"` |

## 📊 Reporting

### Report Types

1. **Cucumber HTML Report**
   - Location: `target/cucumber-reports/cucumber.html`
   - Features: Step-by-step execution, screenshots, timing

2. **Cucumber JSON Report**
   - Location: `target/cucumber-reports/cucumber.json`
   - Usage: CI/CD integration, custom reporting

3. **TestNG XML Report**
   - Location: `target/cucumber-reports/cucumber-testng.xml`
   - Usage: TestNG-compatible reporting

### Viewing Reports

```bash
# Open HTML report in default browser
open target/cucumber-reports/cucumber.html

# Or serve reports locally
python -m http.server 8000 -d target/cucumber-reports/
# Then visit http://localhost:8000
```

### CI/CD Integration

```yaml
# GitHub Actions example
- name: Run Tests
  run: mvn test

- name: Upload Test Reports
  uses: actions/upload-artifact@v3
  with:
    name: test-reports
    path: target/cucumber-reports/
```

## 🎯 Best Practices

### Code Quality

- **Page Object Model**: All web interactions abstracted into page classes
- **Single Responsibility**: Each method has one clear purpose
- **Descriptive Names**: Methods and variables clearly indicate their function
- **Documentation**: Comprehensive JavaDoc and comments

### Test Design

- **Independent Tests**: Each scenario can run independently
- **Clear Scenarios**: Gherkin features are business-readable
- **Data-Driven**: Configuration externalized from code
- **Maintainable**: Easy to update when UI changes

### Performance

- **Parallel Execution**: Tests run concurrently for speed
- **Efficient Waiting**: Smart waits prevent unnecessary delays
- **Resource Cleanup**: Proper browser and driver management

### Debugging

- **Detailed Logging**: Contextual information for troubleshooting
- **Screenshot Capture**: Visual debugging on failures
- **Error Context**: Clear error messages with page state

## 🔄 CI/CD Integration

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    publishCucumberReports(
                        fileIncludePattern: 'target/cucumber-reports/*.json',
                        jsonReportDirectory: 'target/cucumber-reports/'
                    )
                }
            }
        }
    }
}
```

### GitHub Actions

```yaml
name: Test Automation
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: mvn clean test
      - uses: actions/upload-artifact@v3
        if: always()
        with:
          name: test-reports
          path: target/cucumber-reports/
```

## 🤝 Contributing

### Development Workflow

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/your-feature`
3. **Write** tests for your changes
4. **Implement** your feature
5. **Run** the full test suite: `mvn clean test`
6. **Commit** your changes: `git commit -am 'Add your feature'`
7. **Push** to the branch: `git push origin feature/your-feature`
8. **Create** a Pull Request

### Code Standards

- Follow Java naming conventions
- Add JavaDoc for all public methods
- Write descriptive commit messages
- Ensure all tests pass before submitting PR
- Update documentation for API changes

### Pull Request Guidelines

- **Title**: Clear, descriptive title
- **Description**: Detailed explanation of changes
- **Tests**: Include test coverage for new features
- **Documentation**: Update README if needed
- **Breaking Changes**: Clearly mark any breaking changes

## 🔧 Troubleshooting

### Common Issues

#### Browser Driver Issues
```bash
# Clear WebDriverManager cache
rm -rf ~/.cache/selenium

# Force driver download
mvn test -Dwebdrivermanager.forceDownload=true
```

#### Test Timeouts
```bash
# Increase timeout in config.properties
browser.timeout=60

# Or via command line
mvn test -Dbrowser.timeout=60
```

#### Element Not Found
- Check if locators are correct
- Verify page load completion
- Use browser developer tools to inspect elements
- Check for dynamic content loading

#### Parallel Execution Issues
```bash
# Run sequentially for debugging
mvn test -Dparallel=none

# Check thread safety in page objects
```

### Debug Mode

```bash
# Enable debug logging
mvn test -Dlog.level=DEBUG

# Run single test for debugging
mvn test -Dcucumber.filter.tags="@debug"
```

### Getting Help

1. **Check Logs**: Review `target/surefire-reports/` and `logs/`
2. **Enable Screenshots**: Set `screenshot.on.failure=true` in config
3. **Browser DevTools**: Use browser developer tools for element inspection
4. **GitHub Issues**: Report bugs with detailed reproduction steps

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

**Rahul Kain**
- Email: [rahulkain407@gmail.com](mailto:rahulkain407@gmail.com)
- GitHub: [@RahulKain](https://github.com/RahulKain)

### Support

- 📖 [Documentation](https://automationpanda.com/docs/)
- 🐛 [Issue Tracker](https://github.com/RahulKain/automation-panda-framework/issues)
- 💬 [Discussion Forum](https://github.com/RahulKain/automation-panda-framework/discussions)

---

**Built with ❤️ by Rahul Kain**

*Empowering test automation excellence through open source innovation*