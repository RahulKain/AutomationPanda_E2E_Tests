package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;
import utils.DriverManager;

/**
 * CucumberHooks - Cucumber lifecycle hooks for test setup and teardown.
 * Manages WebDriver initialization before each scenario and cleanup after.
 * Handles screenshot capture on test failure for debugging purposes.
 * 
 * <p>These hooks are automatically discovered by Cucumber through the glue path
 * configuration in the test runner.</p>
 * 
 * @author AutomationPanda
 * @version 1.0
 */
public class CucumberHooks {

    private static final Logger logger = LoggerFactory.getLogger(CucumberHooks.class);

    /**
     * Before hook - Executes before each Cucumber scenario.
     * Initializes the WebDriver instance for the current thread.
     * 
     * @param scenario the current Cucumber scenario being executed
     */
    @Before
    public void setUp(Scenario scenario) {
        logger.info("========================================");
        logger.info("Starting Scenario: {}", scenario.getName());
        logger.info("Tags: {}", scenario.getSourceTagNames());
        logger.info("========================================");
        
        // Initialize WebDriver for this scenario
        WebDriver driver = DriverManager.getDriver();
        logger.info("WebDriver initialized successfully for scenario: {}", scenario.getName());
        
        // Navigate to base URL if configured
        String url = ConfigReader.getUrl();
        if (url != null && !url.isEmpty()) {
            logger.info("Navigating to URL: {}", url);
            driver.get(url);
        }
    }

    /**
     * After hook - Executes after each Cucumber scenario.
     * Captures screenshot on failure (if configured) and quits the WebDriver.
     * 
     * @param scenario the current Cucumber scenario that just completed
     */
    @After
    public void tearDown(Scenario scenario) {
        logger.info("----------------------------------------");
        logger.info("Finishing Scenario: {}", scenario.getName());
        logger.info("Status: {}", scenario.getStatus());
        logger.info("----------------------------------------");
        
        try {
            // Capture screenshot on failure if configured
            if (scenario.isFailed() && ConfigReader.takeScreenshotOnFailure()) {
                captureScreenshot(scenario);
            }
        } catch (Exception e) {
            logger.error("Error during teardown: {}", e.getMessage());
        } finally {
            // Always quit the driver to clean up resources
            DriverManager.quitDriver();
            logger.info("Scenario teardown completed: {}", scenario.getName());
        }
    }

    /**
     * Captures a screenshot and attaches it to the Cucumber report.
     * Only captures if a WebDriver instance exists for the current thread.
     * 
     * @param scenario the current Cucumber scenario for attaching the screenshot
     */
    private void captureScreenshot(Scenario scenario) {
        if (!DriverManager.hasDriver()) {
            logger.warn("Cannot capture screenshot - no WebDriver available");
            return;
        }
        
        try {
            logger.info("Capturing screenshot for failed scenario: {}", scenario.getName());
            
            WebDriver driver = DriverManager.getDriver();
            
            if (driver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                
                // Attach screenshot to Cucumber report
                scenario.attach(screenshot, "image/png", "Screenshot on failure - " + scenario.getName());
                
                logger.info("Screenshot captured and attached to report successfully");
            } else {
                logger.warn("WebDriver does not support screenshot capture");
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
        }
    }
}
