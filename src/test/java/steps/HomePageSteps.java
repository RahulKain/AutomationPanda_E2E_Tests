package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.HomePage;
import utils.ConfigReader;
import utils.DriverManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HomePageSteps - Step definitions for homepage feature scenarios.
 * Implements Cucumber steps for testing the Automation Panda homepage.
 * 
 * @author AutomationPanda
 * @version 1.0
 */
public class HomePageSteps {

    private static final Logger logger = LoggerFactory.getLogger(HomePageSteps.class);
    
    private WebDriver driver;
    private HomePage homePage;

    /**
     * Initializes the WebDriver and HomePage objects.
     * Called at the beginning of each step to ensure proper initialization.
     */
    private void initializePageObjects() {
        if (driver == null) {
            driver = DriverManager.getDriver();
            homePage = new HomePage(driver);
        }
    }

    // ==================== Given Steps ====================

    /**
     * Navigates to the Automation Panda homepage.
     * This step is shared across multiple scenarios.
     */
    @Given("I am on the Automation Panda homepage")
    public void iAmOnTheAutomationPandaHomepage() {
        logger.info("Navigating to Automation Panda homepage");
        initializePageObjects();
        
        String url = ConfigReader.getUrl();
        logger.info("URL from config: {}", url);
        
        homePage.navigateTo(url);
        logger.info("Successfully navigated to homepage");
    }

    // ==================== Then Steps ====================

    /**
     * Verifies that the homepage is displayed correctly.
     */
    @Then("the homepage should be displayed")
    public void theHomepageShouldBeDisplayed() {
        logger.info("Verifying homepage is displayed");
        initializePageObjects();
        
        boolean isLoaded = homePage.isHomePageLoaded();
        assertTrue(isLoaded, "Homepage should be loaded and displayed");
        logger.info("Homepage verification passed");
    }

    /**
     * Verifies that the page title contains the expected text.
     * 
     * @param expectedTitle the expected text in the page title
     */
    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expectedTitle) {
        logger.info("Verifying page title contains: {}", expectedTitle);
        initializePageObjects();
        
        String actualTitle = driver.getTitle();
        logger.info("Actual page title: {}", actualTitle);
        
        assertTrue(actualTitle.toLowerCase().contains(expectedTitle.toLowerCase()),
                String.format("Page title should contain '%s' but was '%s'", expectedTitle, actualTitle));
        logger.info("Page title verification passed");
    }

    /**
     * Verifies that the site header is visible on the page.
     */
    @Then("the site header should be visible")
    public void theSiteHeaderShouldBeVisible() {
        logger.info("Verifying site header is visible");
        initializePageObjects();
        
        String headerTitle = homePage.getHeaderTitle();
        assertNotNull(headerTitle, "Site header should be visible");
        assertFalse(headerTitle.isEmpty(), "Site header should not be empty");
        logger.info("Site header is visible: {}", headerTitle);
    }

    /**
     * Verifies that the header displays the expected text.
     * 
     * @param expectedHeader the expected header text
     */
    @Then("the header should display {string}")
    public void theHeaderShouldDisplay(String expectedHeader) {
        logger.info("Verifying header displays: {}", expectedHeader);
        initializePageObjects();
        
        String actualHeader = homePage.getHeaderTitle();
        logger.info("Actual header text: {}", actualHeader);
        
        assertTrue(actualHeader.toLowerCase().contains(expectedHeader.toLowerCase()),
                String.format("Header should display '%s' but was '%s'", expectedHeader, actualHeader));
        logger.info("Header text verification passed");
    }

    /**
     * Verifies that recent blog posts are displayed on the homepage.
     */
    @Then("I should see recent blog posts")
    public void iShouldSeeRecentBlogPosts() {
        logger.info("Verifying recent blog posts are displayed");
        initializePageObjects();
        
        List<String> postTitles = homePage.getRecentPostTitles();
        assertNotNull(postTitles, "Recent posts list should not be null");
        assertFalse(postTitles.isEmpty(), "There should be at least one recent blog post displayed");
        
        logger.info("Found {} recent blog posts", postTitles.size());
        postTitles.forEach(title -> logger.debug("Post: {}", title));
    }

    /**
     * Verifies that at least the specified number of posts are displayed.
     * 
     * @param minPosts the minimum number of posts expected
     */
    @Then("there should be at least {int} post displayed")
    public void thereShouldBeAtLeastPostDisplayed(int minPosts) {
        logger.info("Verifying at least {} post(s) are displayed", minPosts);
        initializePageObjects();
        
        int postCount = homePage.getPostCount();
        logger.info("Actual post count: {}", postCount);
        
        assertTrue(postCount >= minPosts,
                String.format("Expected at least %d post(s) but found %d", minPosts, postCount));
        logger.info("Post count verification passed");
    }
}
