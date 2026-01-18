package steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.DriverManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SearchSteps - Step definitions for search functionality scenarios.
 * Implements Cucumber steps for testing the blog search feature.
 * 
 * @author AutomationPanda
 * @version 1.0
 */
public class SearchSteps {

    private static final Logger logger = LoggerFactory.getLogger(SearchSteps.class);
    
    private WebDriver driver;
    private HomePage homePage;
    private SearchResultsPage searchResultsPage;

    /**
     * Initializes the WebDriver and page objects.
     * Called at the beginning of each step to ensure proper initialization.
     */
    private void initializePageObjects() {
        if (driver == null) {
            driver = DriverManager.getDriver();
            homePage = new HomePage(driver);
            searchResultsPage = new SearchResultsPage(driver);
        }
    }

    // ==================== When Steps ====================

    /**
     * Performs a search for the specified keyword.
     * This step handles both regular scenarios and scenario outlines with parameterized keywords.
     * 
     * @param keyword the search term to look for
     */
    @When("I search for {string}")
    public void iSearchFor(String keyword) {
        logger.info("Performing search for keyword: {}", keyword);
        initializePageObjects();
        
        homePage.searchFor(keyword);
        logger.info("Search submitted for: {}", keyword);
    }

    // ==================== Then Steps ====================

    /**
     * Verifies that search results are displayed on the page.
     */
    @Then("search results should be displayed")
    public void searchResultsShouldBeDisplayed() {
        logger.info("Verifying search results are displayed");
        initializePageObjects();
        
        boolean resultsDisplayed = searchResultsPage.isSearchResultsDisplayed();
        assertTrue(resultsDisplayed, "Search results page should be displayed");
        
        int resultCount = searchResultsPage.getSearchResultsCount();
        logger.info("Search results displayed with {} results", resultCount);
        assertTrue(resultCount > 0, "There should be at least one search result");
    }

    /**
     * Verifies that the search results contain the specified keyword.
     * Checks if the keyword appears in any of the result titles.
     * 
     * @param keyword the keyword expected in the results
     */
    @Then("the results should contain {string}")
    public void theResultsShouldContain(String keyword) {
        logger.info("Verifying results contain keyword: {}", keyword);
        initializePageObjects();
        
        // Check if keyword is in result titles
        boolean keywordFound = searchResultsPage.isKeywordInResults(keyword);
        
        if (!keywordFound) {
            // Log available titles for debugging
            List<String> titles = searchResultsPage.getSearchResultTitles();
            logger.warn("Keyword '{}' not found in titles. Available titles: {}", keyword, titles);
        }
        
        assertTrue(keywordFound,
                String.format("Search results should contain keyword '%s' in at least one title", keyword));
        logger.info("Keyword '{}' found in search results", keyword);
    }

    /**
     * Verifies that no search results are found for the search query.
     * Used for testing searches that should return empty results.
     */
    @Then("no search results should be found")
    public void noSearchResultsShouldBeFound() {
        logger.info("Verifying no search results are found");
        initializePageObjects();
        
        boolean hasNoResults = searchResultsPage.hasNoResults();
        
        if (!hasNoResults) {
            int count = searchResultsPage.getSearchResultsCount();
            List<String> titles = searchResultsPage.getSearchResultTitles();
            logger.warn("Expected no results but found {} results: {}", count, titles);
        }
        
        assertTrue(hasNoResults, "Search should return no results for the given query");
        logger.info("Verified: No search results found as expected");
    }
}
