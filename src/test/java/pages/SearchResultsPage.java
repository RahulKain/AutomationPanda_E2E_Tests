package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchResultsPage - Page Object for the search results page.
 * Represents the page displayed after performing a search on automationpanda.com.
 * 
 * <p>This page displays search results with post titles, excerpts, and links.</p>
 * <p>Uses Selenium PageFactory pattern with @FindBy annotations.</p>
 * <p>Enhanced with detailed error tracking and logging for better debugging.</p>
 * 
 * @author AutomationPanda
 * @version 1.2
 */
public class SearchResultsPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(SearchResultsPage.class);

    // ==================== Element Names for Logging ====================
    private static final String SEARCH_RESULTS_CONTAINER_NAME = "Search Results Container (#main, .site-main, main)";
    private static final String SEARCH_PAGE_TITLE_NAME = "Search Page Title (.page-title, .archive-title, h1.entry-title)";
    private static final String SEARCH_RESULT_ARTICLES_NAME = "Search Result Articles (article.post)";
    private static final String SEARCH_RESULT_TITLES_NAME = "Search Result Titles (article .entry-title)";
    private static final String NO_RESULTS_MESSAGE_NAME = "No Results Message (.no-results, .not-found)";
    private static final String SEARCH_INPUT_NAME = "Search Input (input.search-field[name='s'])";
    private static final String PAGINATION_NAME = "Pagination (.pagination, .nav-links)";
    private static final String NEXT_PAGE_LINK_NAME = "Next Page Link (.next, .nav-next a)";
    private static final String PREV_PAGE_LINK_NAME = "Previous Page Link (.prev, .nav-previous a)";

    // ==================== Page Elements using @FindBy ====================
    
    /** Search results container */
    @FindBy(css = "#main, .site-main, main")
    private WebElement searchResultsContainer;
    
    /** Page header/title showing search query */
    @FindBy(css = ".page-title, .archive-title, h1.entry-title")
    private WebElement searchPageTitle;
    
    /** Individual search result articles */
    @FindBy(css = "article.post, article.type-post, .search-entry")
    private List<WebElement> searchResultArticles;
    
    /** Search result titles */
    @FindBy(css = "article .entry-title, .search-entry .entry-title")
    private List<WebElement> searchResultTitles;
    
    /** Search result excerpts/summaries */
    @FindBy(css = "article .entry-summary, article .entry-content, .search-entry .entry-excerpt")
    private List<WebElement> searchResultExcerpts;
    
    /** No results message */
    @FindBy(css = ".no-results, .not-found, .entry-content p")
    private WebElement noResultsMessage;
    
    /** Search form on results page (for new search) */
    @FindBy(css = "form.search-form, form[role='search']")
    private WebElement searchForm;
    
    /** Search input on results page */
    @FindBy(css = "input.search-field[name='s']")
    private WebElement searchInput;
    
    /** Pagination container */
    @FindBy(css = ".pagination, .nav-links, .page-numbers")
    private WebElement pagination;
    
    /** Next page link */
    @FindBy(css = ".next, .nav-next a, a.next")
    private WebElement nextPageLink;
    
    /** Previous page link */
    @FindBy(css = ".prev, .nav-previous a, a.prev")
    private WebElement prevPageLink;

    // ==================== By Locators for dynamic elements ====================
    
    /** Entry title within articles (for dynamic lookup) */
    private static final By ENTRY_TITLE_LOCATOR = By.cssSelector(".entry-title");

    // ==================== Constructor ====================

    /**
     * Constructor that initializes the SearchResultsPage with a WebDriver instance.
     * PageFactory.initElements is called in the parent BasePage constructor.
     * 
     * @param driver the WebDriver instance to use for browser interactions
     */
    public SearchResultsPage(WebDriver driver) {
        super(driver);
        logger.info("[SearchResultsPage] Page object created for URL: {}", driver.getCurrentUrl());
    }

    // ==================== Page Methods ====================

    /**
     * Verifies that search results are displayed on the page.
     * Uses FluentWait to handle dynamic content loading.
     *
     * @return true if search results are displayed, false otherwise
     */
    public boolean isSearchResultsDisplayed() {
        logger.info("[SearchResultsPage] ========== VERIFICATION ==========");
        logger.info("[SearchResultsPage] Checking if search results are displayed...");
        try {
            waitForPageLoad();

            // Check if we're on a search results page
            String currentUrl = getCurrentUrl();
            boolean isSearchPage = currentUrl.contains("?s=") || currentUrl.contains("search");
            logger.debug("[SearchResultsPage] Is search page URL: {} | URL: {}", isSearchPage, currentUrl);

            // Check for results container
            boolean containerPresent = isDisplayed(searchResultsContainer, SEARCH_RESULTS_CONTAINER_NAME);
            logger.debug("[SearchResultsPage] Container present: {}", containerPresent);

            // Use FluentWait to check for results or no-results message with dynamic polling
            boolean hasResults = fluentWaitForElementToDisappear(
                By.cssSelector("article.post:not(.post)"), "Loading placeholder", 5, 200) &&
                searchResultArticles.size() > 0;

            boolean hasNoResultsMessage = fluentWaitForText(
                By.cssSelector(".no-results, .not-found, .entry-content p"),
                "no results", "No results message", 5, 200);

            logger.debug("[SearchResultsPage] Has results: {} (count: {})", hasResults, searchResultArticles.size());
            logger.debug("[SearchResultsPage] Has no-results message: {}", hasNoResultsMessage);

            boolean isDisplayed = containerPresent && (hasResults || hasNoResultsMessage);

            logger.info("[SearchResultsPage] VERIFICATION RESULT: {} | isSearchPage: {} | containerPresent: {} | hasResults: {} | hasNoResultsMessage: {}",
                    isDisplayed ? "PASS" : "FAIL", isSearchPage, containerPresent, hasResults, hasNoResultsMessage);
            logger.info("[SearchResultsPage] ================================");

            if (!isDisplayed) {
                captureDebugInfo();
            }

            return isDisplayed;
        } catch (Exception e) {
            logger.error("[SearchResultsPage] ERROR checking if search results are displayed: {}", e.getMessage());
            captureDebugInfo();
            return false;
        }
    }

    /**
     * Gets the count of search results displayed on the current page.
     * 
     * @return the number of search results
     */
    public int getSearchResultsCount() {
        logger.info("[SearchResultsPage] Getting search results count...");
        try {
            waitForPageLoad();
            int count = searchResultArticles.size();
            logger.info("[SearchResultsPage] Found {} search results", count);
            return count;
        } catch (Exception e) {
            logger.error("[SearchResultsPage] Error getting search results count: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Gets a list of all search result titles on the current page.
     * 
     * @return list of result title strings
     */
    public List<String> getSearchResultTitles() {
        logger.info("[SearchResultsPage] Getting search result titles...");
        List<String> titles = new ArrayList<>();
        
        try {
            waitForPageLoad();
            
            logger.debug("[SearchResultsPage] Found {} title elements", searchResultTitles.size());
            
            for (int i = 0; i < searchResultTitles.size(); i++) {
                WebElement element = searchResultTitles.get(i);
                String title = element.getText().trim();
                if (!title.isEmpty()) {
                    titles.add(title);
                    logger.debug("[SearchResultsPage] Result {}: '{}'", i + 1, title);
                }
            }
            
            // Fallback: try getting titles from articles directly
            if (titles.isEmpty()) {
                logger.debug("[SearchResultsPage] No titles from direct locator, trying fallback...");
                for (int i = 0; i < searchResultArticles.size(); i++) {
                    WebElement article = searchResultArticles.get(i);
                    try {
                        WebElement titleElement = article.findElement(ENTRY_TITLE_LOCATOR);
                        String title = titleElement.getText().trim();
                        if (!title.isEmpty()) {
                            titles.add(title);
                            logger.debug("[SearchResultsPage] Fallback result {}: '{}'", i + 1, title);
                        }
                    } catch (Exception e) {
                        logger.debug("[SearchResultsPage] Could not get title from article {}", i + 1);
                    }
                }
            }
            
            logger.info("[SearchResultsPage] Found {} search result titles: {}", titles.size(), titles);
        } catch (Exception e) {
            logger.error("[SearchResultsPage] Error getting search result titles: {}", e.getMessage());
            captureDebugInfo();
        }
        
        return titles;
    }

    /**
     * Clicks on a search result by its index (0-based).
     * 
     * @param index the index of the result to click (0 for first result)
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void clickOnResult(int index) {
        logger.info("[SearchResultsPage] ========== CLICK RESULT BY INDEX ==========");
        logger.info("[SearchResultsPage] Clicking on search result at index: {}", index);
        
        try {
            waitForPageLoad();
            List<WebElement> titleElements = new ArrayList<>(searchResultTitles);
            
            if (titleElements.isEmpty()) {
                logger.debug("[SearchResultsPage] No title elements found, trying fallback...");
                for (WebElement article : searchResultArticles) {
                    try {
                        WebElement titleElement = article.findElement(ENTRY_TITLE_LOCATOR);
                        titleElements.add(titleElement);
                    } catch (Exception e) {
                        // Continue
                    }
                }
            }
            
            logger.debug("[SearchResultsPage] Total clickable results: {}", titleElements.size());
            
            if (index < 0 || index >= titleElements.size()) {
                String errorMsg = String.format("[SearchResultsPage] INDEX OUT OF RANGE: Requested index %d, but only %d results available", 
                        index, titleElements.size());
                logger.error(errorMsg);
                captureDebugInfo();
                throw new IndexOutOfBoundsException(errorMsg);
            }
            
            WebElement titleElement = titleElements.get(index);
            String resultTitle = titleElement.getText();

            // Click on the parent <a> element for better reliability
            WebElement linkElement = titleElement.findElement(By.xpath("./ancestor::a[1]"));

            logger.info("[SearchResultsPage] Clicking on result: '{}' at index {}", resultTitle, index);
            scrollToElement(linkElement, "Search Result Link: " + resultTitle);
            linkElement.click();
            
            logger.info("[SearchResultsPage] SUCCESS: Clicked on result '{}' at index {}", resultTitle, index);
            logger.info("[SearchResultsPage] ==========================================");
        } catch (IndexOutOfBoundsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("[SearchResultsPage] FAILED: Error clicking on result at index {}: {}", index, e.getMessage());
            captureDebugInfo();
            throw new RuntimeException("Could not click on search result at index: " + index, e);
        }
    }

    /**
     * Clicks on a search result by its title text.
     * 
     * @param title the title of the result to click
     * @throws RuntimeException if the result is not found
     */
    public void clickOnResultByTitle(String title) {
        logger.info("[SearchResultsPage] ========== CLICK RESULT BY TITLE ==========");
        logger.info("[SearchResultsPage] Looking for result with title: '{}'", title);
        
        try {
            waitForPageLoad();
            
            // Exact match first
            logger.debug("[SearchResultsPage] Trying exact match...");
            for (int i = 0; i < searchResultTitles.size(); i++) {
                WebElement element = searchResultTitles.get(i);
                String foundTitle = element.getText().trim();
                logger.debug("[SearchResultsPage] Result {}: '{}'", i + 1, foundTitle);
                
                if (foundTitle.equalsIgnoreCase(title.trim())) {
                    logger.info("[SearchResultsPage] FOUND (exact match): '{}' at position {}", title, i + 1);
                    // Click on the parent <a> element for better reliability
                    WebElement linkElement = element.findElement(By.xpath("./ancestor::a[1]"));
                    scrollToElement(linkElement, "Search Result Link: " + title);
                    linkElement.click();
                    logger.info("[SearchResultsPage] SUCCESS: Clicked on result: '{}'", title);
                    logger.info("[SearchResultsPage] ==========================================");
                    return;
                }
            }
            
            // Partial match fallback
            logger.debug("[SearchResultsPage] Exact match not found, trying partial match...");
            for (int i = 0; i < searchResultTitles.size(); i++) {
                WebElement element = searchResultTitles.get(i);
                String foundTitle = element.getText().trim();
                
                if (foundTitle.toLowerCase().contains(title.toLowerCase())) {
                    logger.info("[SearchResultsPage] FOUND (partial match): '{}' contains '{}' at position {}", foundTitle, title, i + 1);
                    // Click on the parent <a> element for better reliability
                    WebElement linkElement = element.findElement(By.xpath("./ancestor::a[1]"));
                    scrollToElement(linkElement, "Search Result Link: " + foundTitle);
                    linkElement.click();
                    logger.info("[SearchResultsPage] SUCCESS: Clicked on result (partial match): '{}'", title);
                    logger.info("[SearchResultsPage] ==========================================");
                    return;
                }
            }
            
            logger.error("[SearchResultsPage] FAILED: Search result not found: '{}'", title);
            logger.error("[SearchResultsPage] Available results: {}", getSearchResultTitles());
            captureDebugInfo();
            throw new RuntimeException("Search result not found: " + title);
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("[SearchResultsPage] ERROR clicking on result '{}': {}", title, e.getMessage());
            captureDebugInfo();
            throw new RuntimeException("Could not click on search result: " + title, e);
        }
    }

    /**
     * Gets the search page title/header text.
     * 
     * @return the page title text
     */
    public String getSearchPageTitle() {
        logger.info("[SearchResultsPage] Getting search page title...");
        try {
            String title = getText(searchPageTitle, SEARCH_PAGE_TITLE_NAME);
            logger.info("[SearchResultsPage] Search page title: '{}'", title);
            return title;
        } catch (Exception e) {
            logger.warn("[SearchResultsPage] Could not get search page title: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Checks if there are no search results (empty results).
     * 
     * @return true if no results found, false otherwise
     */
    public boolean hasNoResults() {
        logger.info("[SearchResultsPage] Checking if search returned no results...");
        try {
            waitForPageLoad();
            
            // Check for no results message
            if (isDisplayed(noResultsMessage, NO_RESULTS_MESSAGE_NAME)) {
                String message = getText(noResultsMessage, NO_RESULTS_MESSAGE_NAME);
                boolean noResults = message.toLowerCase().contains("no") || 
                                   message.toLowerCase().contains("nothing") ||
                                   message.toLowerCase().contains("sorry");
                if (noResults) {
                    logger.info("[SearchResultsPage] No results message found: '{}'", message);
                    return true;
                }
            }
            
            // Check if result count is zero
            int count = getSearchResultsCount();
            boolean noResults = count == 0;
            logger.info("[SearchResultsPage] No results status: {} (count: {})", noResults, count);
            return noResults;
            
        } catch (Exception e) {
            logger.error("[SearchResultsPage] Error checking for no results: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Performs a new search from the search results page.
     * Uses FluentWait for more reliable search input interaction.
     *
     * @param keyword the new search term
     */
    public void searchAgain(String keyword) {
        logger.info("[SearchResultsPage] ========== NEW SEARCH ==========");
        logger.info("[SearchResultsPage] Performing new search for: '{}'", keyword);
        try {
            // Use FluentWait to ensure search input is ready
            WebElement readySearchInput = fluentWaitForElementClickable(
                By.cssSelector("input.search-field[name='s']"), SEARCH_INPUT_NAME, 10, 300);

            sendKeys(readySearchInput, SEARCH_INPUT_NAME, keyword);
            readySearchInput.submit();

            logger.info("[SearchResultsPage] SUCCESS: New search submitted for: '{}'", keyword);
            logger.info("[SearchResultsPage] ================================");
        } catch (Exception e) {
            logger.error("[SearchResultsPage] FAILED: Error performing new search: {}", e.getMessage());
            captureDebugInfo();
            throw new RuntimeException("Could not perform new search for: " + keyword, e);
        }
    }

    /**
     * Checks if pagination is available on the results page.
     * 
     * @return true if pagination is present, false otherwise
     */
    public boolean hasPagination() {
        logger.debug("[SearchResultsPage] Checking if pagination is available...");
        boolean hasPagination = isDisplayed(pagination, PAGINATION_NAME);
        logger.debug("[SearchResultsPage] Pagination available: {}", hasPagination);
        return hasPagination;
    }

    /**
     * Clicks the next page link if available.
     * 
     * @return true if next page was clicked, false if not available
     */
    public boolean goToNextPage() {
        logger.info("[SearchResultsPage] Attempting to go to next page...");
        try {
            if (isDisplayed(nextPageLink, NEXT_PAGE_LINK_NAME)) {
                click(nextPageLink, NEXT_PAGE_LINK_NAME);
                waitForPageLoad();
                logger.info("[SearchResultsPage] SUCCESS: Navigated to next page | URL: {}", getCurrentUrl());
                return true;
            }
            logger.info("[SearchResultsPage] Next page link not available");
            return false;
        } catch (Exception e) {
            logger.error("[SearchResultsPage] Error navigating to next page: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Clicks the previous page link if available.
     * 
     * @return true if previous page was clicked, false if not available
     */
    public boolean goToPreviousPage() {
        logger.info("[SearchResultsPage] Attempting to go to previous page...");
        try {
            if (isDisplayed(prevPageLink, PREV_PAGE_LINK_NAME)) {
                click(prevPageLink, PREV_PAGE_LINK_NAME);
                waitForPageLoad();
                logger.info("[SearchResultsPage] SUCCESS: Navigated to previous page | URL: {}", getCurrentUrl());
                return true;
            }
            logger.info("[SearchResultsPage] Previous page link not available");
            return false;
        } catch (Exception e) {
            logger.error("[SearchResultsPage] Error navigating to previous page: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the excerpts/summaries of all search results.
     * 
     * @return list of excerpt strings
     */
    public List<String> getSearchResultExcerpts() {
        logger.info("[SearchResultsPage] Getting search result excerpts...");
        List<String> excerpts = new ArrayList<>();
        
        try {
            for (int i = 0; i < searchResultExcerpts.size(); i++) {
                WebElement element = searchResultExcerpts.get(i);
                String excerpt = element.getText().trim();
                if (!excerpt.isEmpty()) {
                    excerpts.add(excerpt);
                    logger.debug("[SearchResultsPage] Excerpt {}: '{}'", i + 1, 
                            excerpt.substring(0, Math.min(excerpt.length(), 50)) + "...");
                }
            }
            logger.info("[SearchResultsPage] Found {} search result excerpts", excerpts.size());
        } catch (Exception e) {
            logger.error("[SearchResultsPage] Error getting search result excerpts: {}", e.getMessage());
        }
        
        return excerpts;
    }

    /**
     * Checks if a specific keyword appears in any of the search result titles.
     * 
     * @param keyword the keyword to search for
     * @return true if keyword is found in any title, false otherwise
     */
    public boolean isKeywordInResults(String keyword) {
        logger.info("[SearchResultsPage] Checking if keyword '{}' appears in results...", keyword);
        List<String> titles = getSearchResultTitles();
        
        for (String title : titles) {
            if (title.toLowerCase().contains(keyword.toLowerCase())) {
                logger.info("[SearchResultsPage] FOUND: Keyword '{}' found in title: '{}'", keyword, title);
                return true;
            }
        }
        
        logger.info("[SearchResultsPage] NOT FOUND: Keyword '{}' not found in any result titles", keyword);
        logger.debug("[SearchResultsPage] Available titles: {}", titles);
        return false;
    }
}
