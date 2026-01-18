package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * HomePage - Page Object for the Automation Panda homepage.
 * Represents the main page at https://automationpanda.com/
 * 
 * <p>This page contains the site header, navigation menu, search functionality,
 * and recent blog posts.</p>
 * <p>Uses Selenium PageFactory pattern with @FindBy annotations.</p>
 * <p>Enhanced with detailed error tracking and logging for better debugging.</p>
 * 
 * @author AutomationPanda
 * @version 1.2
 */
public class HomePage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(HomePage.class);

    // ==================== Element Names for Logging ====================
    private static final String SITE_TITLE_NAME = "Site Title (h1.site-title)";
    private static final String SITE_DESCRIPTION_NAME = "Site Description (h2.site-description)";
    private static final String NAV_MENU_NAME = "Navigation Menu (nav#site-navigation)";
    private static final String NAV_MENU_ITEMS_NAME = "Navigation Menu Items (#menu-primary .menu-item a)";
    private static final String SEARCH_ICON_NAME = "Search Submit Button (input.search-submit)";
    private static final String SEARCH_INPUT_NAME = "Search Input Field (input.search-field[name='s'])";
    private static final String SEARCH_FORM_NAME = "Search Form (form.search-form)";
    private static final String MAIN_CONTENT_NAME = "Main Content (#main, .site-main, main)";
    private static final String FOOTER_NAME = "Footer (footer.site-footer, #colophon)";
    private static final String ARTICLE_ENTRIES_NAME = "Article Entries (article.post)";

    // ==================== Page Elements using @FindBy ====================
    
    /** Site header/title element */
    @FindBy(css = "h1.site-title")
    private WebElement siteTitle;
    
    /** Site tagline/description element */
    @FindBy(css = "h2.site-description")
    private WebElement siteDescription;
    
    /** Main navigation menu element */
    @FindBy(css = "nav#site-navigation.main-navigation")
    private WebElement navMenu;
    
    /** Navigation menu items */
    @FindBy(css = "#menu-primary .menu-item a")
    private List<WebElement> navMenuItems;
    
    /** Search submit button element */
    @FindBy(css = "input.search-submit")
    private WebElement searchIcon;
    
    /** Search input field element */
    @FindBy(css = "input.search-field[name='s']")
    private WebElement searchInput;
    
    /** Search form element */
    @FindBy(css = "form.search-form")
    private WebElement searchForm;
    
    /** Recent posts section element */
    @FindBy(css = ".widget_recent_entries, article.post, .post")
    private WebElement recentPostsSection;
    
    /** Recent post titles */
    @FindBy(css = "article.post .entry-title")
    private List<WebElement> recentPostTitles;
    
    /** All article entries on the page */
    @FindBy(css = "article.post, article.type-post")
    private List<WebElement> articleEntries;
    
    /** Main content area */
    @FindBy(css = "#main, .site-main, main")
    private WebElement mainContent;
    
    /** Footer area */
    @FindBy(css = "footer.site-footer, #colophon")
    private WebElement footer;

    // ==================== By Locators for dynamic elements ====================

    /** Search input field locator for dynamic waiting */
    private static final By SEARCH_INPUT = By.cssSelector("input.search-field[name='s']");

    /** Entry title within articles (for dynamic lookup) */
    private static final By ENTRY_TITLE_LOCATOR = By.cssSelector(".entry-title");

    // ==================== Constructor ====================

    /**
     * Constructor that initializes the HomePage with a WebDriver instance.
     * PageFactory.initElements is called in the parent BasePage constructor.
     * 
     * @param driver the WebDriver instance to use for browser interactions
     */
    public HomePage(WebDriver driver) {
        super(driver);
        logger.info("[HomePage] Page object created for URL: {}", driver.getCurrentUrl());
    }

    // ==================== Page Methods ====================

    /**
     * Verifies that the homepage is loaded by checking for key elements.
     * 
     * @return true if the homepage is loaded, false otherwise
     */
    public boolean isHomePageLoaded() {
        logger.info("[HomePage] Verifying homepage is loaded...");
        try {
            waitForPageLoad();
            
            boolean titlePresent = isDisplayed(siteTitle, SITE_TITLE_NAME);
            boolean contentPresent = isDisplayed(mainContent, MAIN_CONTENT_NAME);
            boolean isLoaded = titlePresent && contentPresent;
            
            if (isLoaded) {
                logger.info("[HomePage] VERIFIED: Homepage loaded successfully | Title: {} | Content: {}", titlePresent, contentPresent);
            } else {
                logger.warn("[HomePage] VERIFICATION FAILED: Homepage may not be fully loaded | Title present: {} | Content present: {}", 
                        titlePresent, contentPresent);
                captureDebugInfo();
            }
            return isLoaded;
        } catch (Exception e) {
            logger.error("[HomePage] ERROR checking if homepage is loaded: {}", e.getMessage());
            captureDebugInfo();
            return false;
        }
    }

    /**
     * Gets the site header title text.
     * 
     * @return the header title text
     */
    public String getHeaderTitle() {
        logger.info("[HomePage] Getting header title...");
        String title = getText(siteTitle, SITE_TITLE_NAME);
        logger.info("[HomePage] Header title retrieved: '{}'", title);
        return title;
    }

    /**
     * Gets the site description/tagline text.
     * 
     * @return the site description text, or empty string if not found
     */
    public String getSiteDescription() {
        logger.info("[HomePage] Getting site description...");
        try {
            String description = getText(siteDescription, SITE_DESCRIPTION_NAME);
            logger.info("[HomePage] Site description retrieved: '{}'", description);
            return description;
        } catch (Exception e) {
            logger.warn("[HomePage] Site description not found: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Clicks on the search icon to open the search functionality.
     * Handles different search UI implementations.
     */
    public void clickSearchIcon() {
        logger.info("[HomePage] Clicking search icon...");
        try {
            if (isDisplayed(searchIcon, SEARCH_ICON_NAME)) {
                click(searchIcon, SEARCH_ICON_NAME);
                logger.info("[HomePage] Search icon clicked successfully");
            } else {
                logger.debug("[HomePage] Search icon not found, search may already be visible");
            }
        } catch (Exception e) {
            logger.warn("[HomePage] Could not click search icon: {}", e.getMessage());
        }
    }

    /**
     * Performs a search for the specified keyword.
     * Opens search if needed, enters the keyword, and submits.
     * Uses FluentWait for more flexible search input detection.
     *
     * @param keyword the search term to look for
     */
    public void searchFor(String keyword) {
        logger.info("[HomePage] ========== SEARCH OPERATION ==========");
        logger.info("[HomePage] Searching for keyword: '{}'", keyword);

        // Try to click search icon first (if search is hidden)
        clickSearchIcon();

        // Use FluentWait to wait for search input with custom polling
        try {
            logger.info("[HomePage] FluentWait: Waiting for search input to be clickable...");
            WebElement visibleSearchInput = fluentWaitForElementClickable(SEARCH_INPUT, SEARCH_INPUT_NAME, 15, 300);

            logger.info("[HomePage] Clearing search input...");
            visibleSearchInput.clear();

            logger.info("[HomePage] Typing keyword '{}' into search input...", keyword);
            visibleSearchInput.sendKeys(keyword);

            logger.info("[HomePage] Submitting search form...");
            visibleSearchInput.submit();

            logger.info("[HomePage] SUCCESS: Search submitted for keyword: '{}'", keyword);
            logger.info("[HomePage] ========================================");
        } catch (Exception e) {
            logger.error("[HomePage] FAILED: Search operation failed for keyword: '{}'", keyword);
            logger.error("[HomePage] Error details: {}", e.getMessage());
            captureDebugInfo();
            throw new RuntimeException("Could not perform search for: " + keyword, e);
        }
    }

    /**
     * Gets a list of recent post titles displayed on the homepage.
     * 
     * @return list of post title strings
     */
    public List<String> getRecentPostTitles() {
        logger.info("[HomePage] Getting recent post titles...");
        List<String> titles = new ArrayList<>();
        
        try {
            waitForPageLoad();
            
            logger.debug("[HomePage] Found {} article entries", articleEntries.size());
            
            for (int i = 0; i < articleEntries.size(); i++) {
                WebElement article = articleEntries.get(i);
                try {
                    WebElement titleElement = article.findElement(ENTRY_TITLE_LOCATOR);
                    String title = titleElement.getText().trim();
                    if (!title.isEmpty()) {
                        titles.add(title);
                        logger.debug("[HomePage] Article {}: '{}'", i + 1, title);
                    }
                } catch (Exception e) {
                    logger.debug("[HomePage] Could not get title from article {}: {}", i + 1, e.getMessage());
                }
            }
            
            // Fallback: try direct locator if no titles found
            if (titles.isEmpty()) {
                logger.debug("[HomePage] No titles from articles, trying fallback locator...");
                for (WebElement element : recentPostTitles) {
                    String title = element.getText().trim();
                    if (!title.isEmpty()) {
                        titles.add(title);
                    }
                }
            }
            
            logger.info("[HomePage] Found {} recent post titles", titles.size());
        } catch (Exception e) {
            logger.error("[HomePage] Error getting recent post titles: {}", e.getMessage());
            captureDebugInfo();
        }
        
        return titles;
    }

    /**
     * Clicks on a specific post by its title.
     * 
     * @param postTitle the title of the post to click
     * @throws RuntimeException if the post is not found
     */
    public void clickOnPost(String postTitle) {
        logger.info("[HomePage] ========== CLICK POST OPERATION ==========");
        logger.info("[HomePage] Looking for post: '{}'", postTitle);
        
        try {
            // Find all article entries
            logger.debug("[HomePage] Searching through {} articles...", articleEntries.size());
            
            for (int i = 0; i < articleEntries.size(); i++) {
                WebElement article = articleEntries.get(i);
                try {
                    WebElement titleElement = article.findElement(ENTRY_TITLE_LOCATOR);
                    String foundTitle = titleElement.getText().trim();
                    logger.debug("[HomePage] Article {}: '{}'", i + 1, foundTitle);
                    
                    if (foundTitle.equalsIgnoreCase(postTitle.trim())) {
                        logger.info("[HomePage] FOUND: Post '{}' at position {}", postTitle, i + 1);
                        // Click on the parent <a> element for better reliability
                        WebElement linkElement = titleElement.findElement(By.xpath("./ancestor::a[1]"));
                        scrollToElement(linkElement, "Post Link: " + postTitle);
                        linkElement.click();
                        logger.info("[HomePage] SUCCESS: Clicked on post: '{}'", postTitle);
                        logger.info("[HomePage] ==========================================");
                        return;
                    }
                } catch (Exception e) {
                    // Continue to next article
                }
            }
            
            // Fallback: try using XPath with contains
            logger.debug("[HomePage] Post not found in articles, trying XPath fallback...");
            By postLocator = By.xpath("//a[contains(text(), '" + postTitle + "')]");
            if (isDisplayed(postLocator)) {
                click(postLocator);
                logger.info("[HomePage] SUCCESS: Clicked on post using fallback: '{}'", postTitle);
                logger.info("[HomePage] ==========================================");
                return;
            }
            
            logger.error("[HomePage] FAILED: Post not found: '{}'", postTitle);
            captureDebugInfo();
            throw new RuntimeException("Post not found: " + postTitle);
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("[HomePage] ERROR clicking on post '{}': {}", postTitle, e.getMessage());
            captureDebugInfo();
            throw new RuntimeException("Could not click on post: " + postTitle, e);
        }
    }

    /**
     * Gets the list of navigation menu items.
     * 
     * @return list of menu item texts
     */
    public List<String> getNavigationMenuItems() {
        logger.info("[HomePage] Getting navigation menu items...");
        List<String> menuItems = new ArrayList<>();
        
        try {
            logger.debug("[HomePage] Found {} menu items", navMenuItems.size());
            
            for (int i = 0; i < navMenuItems.size(); i++) {
                WebElement item = navMenuItems.get(i);
                String text = item.getText().trim();
                if (!text.isEmpty()) {
                    menuItems.add(text);
                    logger.debug("[HomePage] Menu item {}: '{}'", i + 1, text);
                }
            }
            logger.info("[HomePage] Found {} navigation menu items: {}", menuItems.size(), menuItems);
        } catch (Exception e) {
            logger.error("[HomePage] Error getting navigation menu items: {}", e.getMessage());
            captureDebugInfo();
        }
        
        return menuItems;
    }

    /**
     * Clicks on a navigation menu item by its text.
     * 
     * @param menuItemText the text of the menu item to click
     */
    public void clickNavigationMenuItem(String menuItemText) {
        logger.info("[HomePage] Clicking navigation menu item: '{}'", menuItemText);
        By menuItemLocator = By.xpath("//nav//a[contains(text(), '" + menuItemText + "')]");
        click(menuItemLocator);
        logger.info("[HomePage] SUCCESS: Clicked menu item: '{}'", menuItemText);
    }

    /**
     * Checks if the navigation menu is displayed.
     * 
     * @return true if navigation menu is visible, false otherwise
     */
    public boolean isNavigationMenuDisplayed() {
        logger.debug("[HomePage] Checking if navigation menu is displayed...");
        boolean displayed = isDisplayed(navMenu, NAV_MENU_NAME);
        logger.debug("[HomePage] Navigation menu displayed: {}", displayed);
        return displayed;
    }

    /**
     * Checks if the footer is displayed.
     * 
     * @return true if footer is visible, false otherwise
     */
    public boolean isFooterDisplayed() {
        logger.debug("[HomePage] Checking if footer is displayed...");
        boolean displayed = isDisplayed(footer, FOOTER_NAME);
        logger.debug("[HomePage] Footer displayed: {}", displayed);
        return displayed;
    }

    /**
     * Gets the number of posts displayed on the homepage.
     * 
     * @return the count of posts
     */
    public int getPostCount() {
        logger.info("[HomePage] Getting post count...");
        try {
            int count = articleEntries.size();
            logger.info("[HomePage] Found {} posts on homepage", count);
            return count;
        } catch (Exception e) {
            logger.error("[HomePage] Error getting post count: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Navigates to the homepage URL.
     * 
     * @param url the homepage URL to navigate to
     */
    public void navigateTo(String url) {
        logger.info("[HomePage] ========== NAVIGATION ==========");
        logger.info("[HomePage] Navigating to: {}", url);
        driver.get(url);
        waitForPageLoad();
        logger.info("[HomePage] Navigation complete | Current URL: {}", driver.getCurrentUrl());
        logger.info("[HomePage] ================================");
    }
}
