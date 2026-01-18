package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestRunner - Cucumber test runner configuration class.
 * This class configures and executes Cucumber BDD tests using TestNG.
 * 
 * <p>Configuration includes:</p>
 * <ul>
 *   <li>Feature files location</li>
 *   <li>Step definitions and hooks packages (glue)</li>
 *   <li>Report generation plugins</li>
 *   <li>Console output formatting</li>
 *   <li>Tag-based test filtering</li>
 * </ul>
 * 
 * <p>To run specific tests, use tags via command line:</p>
 * <pre>mvn test -Dcucumber.filter.tags="@smoke"</pre>
 * 
 * @author AutomationPanda
 * @version 1.0
 */
@CucumberOptions(
        // Path to feature files
        features = "src/test/java/features",
        
        // Packages containing step definitions and hooks
        glue = {"steps", "hooks"},
        
        // Report plugins configuration
        plugin = {
                "pretty",                                                    // Console output formatting
                "html:target/cucumber-reports/cucumber.html",               // HTML report
                "json:target/cucumber-reports/cucumber.json",               // JSON report for CI integration
                "testng:target/cucumber-reports/cucumber-testng.xml"        // TestNG XML report
        },
        
        // Monochrome output for better readability in console
        monochrome = true,
        
        // Publish report to Cucumber Reports service (disabled by default)
        publish = false,
        
        // Dry run mode - validates step definitions without executing tests
        // Set to true to check if all steps have matching definitions
        dryRun = false,
        
        // Tags filter - can be overridden via command line: -Dcucumber.filter.tags="@smoke"
        // Empty string means run all scenarios
        tags = "@homepage"
)
public class TestRunner extends AbstractTestNGCucumberTests {
    // This class serves as the entry point for Cucumber test execution with TestNG.
    // It extends AbstractTestNGCucumberTests to integrate Cucumber with TestNG.
    // 
    // Usage examples:
    // - Run all tests: mvn test
    // - Run smoke tests: mvn test -Dcucumber.filter.tags="@smoke"
    // - Run regression tests: mvn test -Dcucumber.filter.tags="@regression"
    // - Exclude WIP tests: mvn test -Dcucumber.filter.tags="not @wip"
    // - Run multiple tags: mvn test -Dcucumber.filter.tags="@smoke and @login"
    
    /**
     * Override the scenarios DataProvider to enable parallel execution of scenarios.
     * By default, scenarios run sequentially. To enable parallel execution,
     * set parallel = true in the @DataProvider annotation.
     * 
     * @return Object[][] containing scenario data for TestNG
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
