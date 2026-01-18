package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Function;

/**
 * BasePage - Abstract base class for all Page Objects.
 * Provides common utility methods for interacting with web elements
 * using explicit waits, proper exception handling, and PageFactory pattern.
 * 
 * <p>All page classes should extend this class to inherit common functionality.</p>
 * <p>Uses Selenium PageFactory for element initialization with @FindBy annotations.</p>
 * <p>Enhanced with detailed error tracking and logging for better debugging.</p>
 * 
 * @author AutomationPanda
 * @version 1.2
 */
public abstract class BasePage {

    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    
    /** Default timeout in seconds for explicit waits */
    protected static final int DEFAULT_TIMEOUT = 10;
    
    /** WebDriver instance for browser interactions */
    protected final WebDriver driver;
    
    /** WebDriverWait instance for explicit waits */
    protected final WebDriverWait wait;

    /** FluentWait instance for more flexible waiting */
    protected final FluentWait<WebDriver> fluentWait;

    /** Page name for logging purposes */
    protected final String pageName;

    /**
     * Constructor that initializes the page with a WebDriver instance.
     * Uses PageFactory to initialize all @FindBy annotated WebElements.
     * 
     * @param driver the WebDriver instance to use for browser interactions
     */
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        this.fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        this.pageName = this.getClass().getSimpleName();
        PageFactory.initElements(driver, this);
        logger.info("[{}] Page initialized with PageFactory and FluentWait | URL: {}", pageName, driver.getCurrentUrl());
    }

    /**
     * Gets element description for logging purposes.
     * Attempts to extract useful information from the element.
     * 
     * @param element the WebElement to describe
     * @return a descriptive string for the element
     */
    private String getElementDescription(WebElement element) {
        try {
            String tagName = element.getTagName();
            String id = element.getAttribute("id");
            String className = element.getAttribute("class");
            String name = element.getAttribute("name");
            String text = element.getText();
            
            StringBuilder desc = new StringBuilder();
            desc.append("<").append(tagName);
            if (id != null && !id.isEmpty()) desc.append(" id='").append(id).append("'");
            if (name != null && !name.isEmpty()) desc.append(" name='").append(name).append("'");
            if (className != null && !className.isEmpty()) desc.append(" class='").append(className.substring(0, Math.min(className.length(), 30))).append("'");
            desc.append(">");
            if (text != null && !text.isEmpty()) desc.append(" text='").append(text.substring(0, Math.min(text.length(), 20))).append("'");
            
            return desc.toString();
        } catch (Exception e) {
            return "[Unable to describe element]";
        }
    }

    /**
     * Logs the current page state for debugging.
     */
    protected void logPageState() {
        try {
            logger.debug("[{}] Current URL: {} | Title: {}", pageName, driver.getCurrentUrl(), driver.getTitle());
        } catch (Exception e) {
            logger.warn("[{}] Unable to log page state: {}", pageName, e.getMessage());
        }
    }

    /**
     * Waits for an element to be visible on the page.
     * 
     * @param element the WebElement to wait for
     * @param elementName descriptive name for logging
     * @param timeout the maximum time to wait in seconds
     * @return the WebElement once it is visible
     * @throws TimeoutException if the element is not visible within the timeout
     */
    protected WebElement waitForElementVisible(WebElement element, String elementName, int timeout) {
        logger.debug("[{}] Waiting for '{}' to be visible (timeout: {}s)", pageName, elementName, timeout);
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            WebElement visibleElement = customWait.until(ExpectedConditions.visibilityOf(element));
            logger.debug("[{}] '{}' is now visible: {}", pageName, elementName, getElementDescription(visibleElement));
            return visibleElement;
        } catch (TimeoutException e) {
            logPageState();
            String errorMsg = String.format("[%s] TIMEOUT: Element '%s' not visible within %d seconds. Current URL: %s", 
                    pageName, elementName, timeout, driver.getCurrentUrl());
            logger.error(errorMsg);
            throw new TimeoutException(errorMsg, e);
        } catch (StaleElementReferenceException e) {
            String errorMsg = String.format("[%s] STALE ELEMENT: '%s' became stale while waiting for visibility", pageName, elementName);
            logger.error(errorMsg);
            throw new StaleElementReferenceException(errorMsg, e);
        }
    }

    /**
     * Waits for an element to be visible on the page.
     * 
     * @param element the WebElement to wait for
     * @param timeout the maximum time to wait in seconds
     * @return the WebElement once it is visible
     */
    protected WebElement waitForElementVisible(WebElement element, int timeout) {
        return waitForElementVisible(element, getElementDescription(element), timeout);
    }

    /**
     * Waits for an element to be visible using the default timeout.
     * 
     * @param element the WebElement to wait for
     * @return the WebElement once it is visible
     */
    protected WebElement waitForElementVisible(WebElement element) {
        return waitForElementVisible(element, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for an element to be visible on the page using By locator.
     * 
     * @param locator the By locator to find the element
     * @param timeout the maximum time to wait in seconds
     * @return the WebElement once it is visible
     * @throws TimeoutException if the element is not visible within the timeout
     */
    protected WebElement waitForElementVisible(By locator, int timeout) {
        logger.debug("[{}] Waiting for element '{}' to be visible (timeout: {}s)", pageName, locator, timeout);
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            WebElement element = customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.debug("[{}] Element '{}' is now visible: {}", pageName, locator, getElementDescription(element));
            return element;
        } catch (TimeoutException e) {
            logPageState();
            String errorMsg = String.format("[%s] TIMEOUT: Element '%s' not visible within %d seconds. Current URL: %s", 
                    pageName, locator, timeout, driver.getCurrentUrl());
            logger.error(errorMsg);
            throw new TimeoutException(errorMsg, e);
        }
    }

    /**
     * Waits for an element to be visible using the default timeout.
     * 
     * @param locator the By locator to find the element
     * @return the WebElement once it is visible
     */
    protected WebElement waitForElementVisible(By locator) {
        return waitForElementVisible(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for an element to be clickable on the page.
     * 
     * @param element the WebElement to wait for
     * @param elementName descriptive name for logging
     * @param timeout the maximum time to wait in seconds
     * @return the WebElement once it is clickable
     * @throws TimeoutException if the element is not clickable within the timeout
     */
    protected WebElement waitForElementClickable(WebElement element, String elementName, int timeout) {
        logger.debug("[{}] Waiting for '{}' to be clickable (timeout: {}s)", pageName, elementName, timeout);
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            WebElement clickableElement = customWait.until(ExpectedConditions.elementToBeClickable(element));
            logger.debug("[{}] '{}' is now clickable", pageName, elementName);
            return clickableElement;
        } catch (TimeoutException e) {
            logPageState();
            String errorMsg = String.format("[%s] TIMEOUT: Element '%s' not clickable within %d seconds. Current URL: %s", 
                    pageName, elementName, timeout, driver.getCurrentUrl());
            logger.error(errorMsg);
            throw new TimeoutException(errorMsg, e);
        } catch (StaleElementReferenceException e) {
            String errorMsg = String.format("[%s] STALE ELEMENT: '%s' became stale while waiting for clickability", pageName, elementName);
            logger.error(errorMsg);
            throw new StaleElementReferenceException(errorMsg, e);
        }
    }

    /**
     * Waits for an element to be clickable on the page.
     * 
     * @param element the WebElement to wait for
     * @param timeout the maximum time to wait in seconds
     * @return the WebElement once it is clickable
     */
    protected WebElement waitForElementClickable(WebElement element, int timeout) {
        return waitForElementClickable(element, getElementDescription(element), timeout);
    }

    /**
     * Waits for an element to be clickable using the default timeout.
     * 
     * @param element the WebElement to wait for
     * @return the WebElement once it is clickable
     */
    protected WebElement waitForElementClickable(WebElement element) {
        return waitForElementClickable(element, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for an element to be clickable on the page using By locator.
     * 
     * @param locator the By locator to find the element
     * @param timeout the maximum time to wait in seconds
     * @return the WebElement once it is clickable
     * @throws TimeoutException if the element is not clickable within the timeout
     */
    protected WebElement waitForElementClickable(By locator, int timeout) {
        logger.debug("[{}] Waiting for element '{}' to be clickable (timeout: {}s)", pageName, locator, timeout);
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            WebElement element = customWait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("[{}] Element '{}' is now clickable", pageName, locator);
            return element;
        } catch (TimeoutException e) {
            logPageState();
            String errorMsg = String.format("[%s] TIMEOUT: Element '%s' not clickable within %d seconds. Current URL: %s", 
                    pageName, locator, timeout, driver.getCurrentUrl());
            logger.error(errorMsg);
            throw new TimeoutException(errorMsg, e);
        }
    }

    /**
     * Waits for an element to be clickable using the default timeout.
     * 
     * @param locator the By locator to find the element
     * @return the WebElement once it is clickable
     */
    protected WebElement waitForElementClickable(By locator) {
        return waitForElementClickable(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Clicks on a WebElement after waiting for it to be clickable.
     * 
     * @param element the WebElement to click
     * @param elementName descriptive name for logging
     */
    protected void click(WebElement element, String elementName) {
        logger.info("[{}] ACTION: Clicking on '{}'", pageName, elementName);
        try {
            WebElement clickableElement = waitForElementClickable(element, elementName, DEFAULT_TIMEOUT);
            clickableElement.click();
            logger.info("[{}] SUCCESS: Clicked on '{}'", pageName, elementName);
        } catch (StaleElementReferenceException e) {
            logger.warn("[{}] RETRY: Stale element '{}', retrying click", pageName, elementName);
            WebElement clickableElement = waitForElementClickable(element, elementName, DEFAULT_TIMEOUT);
            clickableElement.click();
            logger.info("[{}] SUCCESS: Clicked on '{}' after retry", pageName, elementName);
        } catch (Exception e) {
            logPageState();
            String errorMsg = String.format("[%s] FAILED: Could not click on '%s'. Error: %s", pageName, elementName, e.getMessage());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Clicks on a WebElement after waiting for it to be clickable.
     * 
     * @param element the WebElement to click
     */
    protected void click(WebElement element) {
        click(element, getElementDescription(element));
    }

    /**
     * Clicks on an element after waiting for it to be clickable using By locator.
     * 
     * @param locator the By locator to find the element
     */
    protected void click(By locator) {
        logger.info("[{}] ACTION: Clicking on element '{}'", pageName, locator);
        try {
            WebElement element = waitForElementClickable(locator);
            element.click();
            logger.info("[{}] SUCCESS: Clicked on element '{}'", pageName, locator);
        } catch (StaleElementReferenceException e) {
            logger.warn("[{}] RETRY: Stale element '{}', retrying click", pageName, locator);
            WebElement element = waitForElementClickable(locator);
            element.click();
            logger.info("[{}] SUCCESS: Clicked on element '{}' after retry", pageName, locator);
        } catch (Exception e) {
            logPageState();
            String errorMsg = String.format("[%s] FAILED: Could not click on '%s'. Error: %s", pageName, locator, e.getMessage());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Clears the field and sends keys to a WebElement after waiting for it to be visible.
     * 
     * @param element the WebElement to send keys to
     * @param elementName descriptive name for logging
     * @param text the text to type into the element
     */
    protected void sendKeys(WebElement element, String elementName, String text) {
        logger.info("[{}] ACTION: Typing '{}' into '{}'", pageName, text, elementName);
        try {
            WebElement visibleElement = waitForElementVisible(element, elementName, DEFAULT_TIMEOUT);
            visibleElement.clear();
            visibleElement.sendKeys(text);
            logger.info("[{}] SUCCESS: Typed '{}' into '{}'", pageName, text, elementName);
        } catch (StaleElementReferenceException e) {
            logger.warn("[{}] RETRY: Stale element '{}', retrying sendKeys", pageName, elementName);
            WebElement visibleElement = waitForElementVisible(element, elementName, DEFAULT_TIMEOUT);
            visibleElement.clear();
            visibleElement.sendKeys(text);
            logger.info("[{}] SUCCESS: Typed '{}' into '{}' after retry", pageName, text, elementName);
        } catch (Exception e) {
            logPageState();
            String errorMsg = String.format("[%s] FAILED: Could not type into '%s'. Error: %s", pageName, elementName, e.getMessage());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Clears the field and sends keys to a WebElement after waiting for it to be visible.
     * 
     * @param element the WebElement to send keys to
     * @param text the text to type into the element
     */
    protected void sendKeys(WebElement element, String text) {
        sendKeys(element, getElementDescription(element), text);
    }

    /**
     * Clears the field and sends keys to an element after waiting for it to be visible using By locator.
     * 
     * @param locator the By locator to find the element
     * @param text the text to type into the element
     */
    protected void sendKeys(By locator, String text) {
        logger.info("[{}] ACTION: Typing '{}' into element '{}'", pageName, text, locator);
        try {
            WebElement element = waitForElementVisible(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("[{}] SUCCESS: Typed '{}' into element '{}'", pageName, text, locator);
        } catch (StaleElementReferenceException e) {
            logger.warn("[{}] RETRY: Stale element '{}', retrying sendKeys", pageName, locator);
            WebElement element = waitForElementVisible(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("[{}] SUCCESS: Typed '{}' into element '{}' after retry", pageName, text, locator);
        } catch (Exception e) {
            logPageState();
            String errorMsg = String.format("[%s] FAILED: Could not type into '%s'. Error: %s", pageName, locator, e.getMessage());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Gets the visible text of a WebElement after waiting for it to be visible.
     * 
     * @param element the WebElement to get text from
     * @param elementName descriptive name for logging
     * @return the visible text of the element
     */
    protected String getText(WebElement element, String elementName) {
        logger.debug("[{}] Getting text from '{}'", pageName, elementName);
        try {
            WebElement visibleElement = waitForElementVisible(element, elementName, DEFAULT_TIMEOUT);
            String text = visibleElement.getText();
            logger.debug("[{}] Got text '{}' from '{}'", pageName, text, elementName);
            return text;
        } catch (Exception e) {
            logPageState();
            String errorMsg = String.format("[%s] FAILED: Could not get text from '%s'. Error: %s", pageName, elementName, e.getMessage());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Gets the visible text of a WebElement after waiting for it to be visible.
     * 
     * @param element the WebElement to get text from
     * @return the visible text of the element
     */
    protected String getText(WebElement element) {
        return getText(element, getElementDescription(element));
    }

    /**
     * Gets the visible text of an element after waiting for it to be visible using By locator.
     * 
     * @param locator the By locator to find the element
     * @return the visible text of the element
     */
    protected String getText(By locator) {
        logger.debug("[{}] Getting text from element '{}'", pageName, locator);
        try {
            WebElement element = waitForElementVisible(locator);
            String text = element.getText();
            logger.debug("[{}] Got text '{}' from element '{}'", pageName, text, locator);
            return text;
        } catch (Exception e) {
            logPageState();
            String errorMsg = String.format("[%s] FAILED: Could not get text from '%s'. Error: %s", pageName, locator, e.getMessage());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Checks if a WebElement is displayed on the page.
     * Does not throw an exception if the element is not found.
     * 
     * @param element the WebElement to check
     * @param elementName descriptive name for logging
     * @return true if the element is displayed, false otherwise
     */
    protected boolean isDisplayed(WebElement element, String elementName) {
        logger.debug("[{}] Checking if '{}' is displayed", pageName, elementName);
        try {
            boolean displayed = element.isDisplayed();
            logger.debug("[{}] '{}' displayed: {}", pageName, elementName, displayed);
            return displayed;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.debug("[{}] '{}' not found or stale", pageName, elementName);
            return false;
        }
    }

    /**
     * Checks if a WebElement is displayed on the page.
     * Does not throw an exception if the element is not found.
     * 
     * @param element the WebElement to check
     * @return true if the element is displayed, false otherwise
     */
    protected boolean isDisplayed(WebElement element) {
        return isDisplayed(element, getElementDescription(element));
    }

    /**
     * Checks if an element is displayed on the page using By locator.
     * Does not throw an exception if the element is not found.
     * 
     * @param locator the By locator to find the element
     * @return true if the element is displayed, false otherwise
     */
    protected boolean isDisplayed(By locator) {
        logger.debug("[{}] Checking if element '{}' is displayed", pageName, locator);
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
            WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            boolean displayed = element.isDisplayed();
            logger.debug("[{}] Element '{}' displayed: {}", pageName, locator, displayed);
            return displayed;
        } catch (TimeoutException | NoSuchElementException e) {
            logger.debug("[{}] Element '{}' not found or not displayed", pageName, locator);
            return false;
        }
    }

    /**
     * Scrolls the page to bring a WebElement into view.
     * 
     * @param element the WebElement to scroll to
     * @param elementName descriptive name for logging
     */
    protected void scrollToElement(WebElement element, String elementName) {
        logger.debug("[{}] Scrolling to '{}'", pageName, elementName);
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            logger.debug("[{}] Scrolled to '{}'", pageName, elementName);
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("[{}] Scroll to '{}' interrupted", pageName, elementName);
        } catch (Exception e) {
            logger.warn("[{}] Could not scroll to '{}': {}", pageName, elementName, e.getMessage());
        }
    }

    /**
     * Scrolls the page to bring a WebElement into view.
     * 
     * @param element the WebElement to scroll to
     */
    protected void scrollToElement(WebElement element) {
        scrollToElement(element, getElementDescription(element));
    }

    /**
     * Scrolls the page to bring an element into view using By locator.
     * 
     * @param locator the By locator to find the element
     */
    protected void scrollToElement(By locator) {
        logger.debug("[{}] Scrolling to element '{}'", pageName, locator);
        try {
            WebElement element = waitForElementVisible(locator);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            logger.debug("[{}] Scrolled to element '{}'", pageName, locator);
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("[{}] Scroll to '{}' interrupted", pageName, locator);
        } catch (Exception e) {
            logger.warn("[{}] Could not scroll to '{}': {}", pageName, locator, e.getMessage());
        }
    }

    /**
     * Gets the title of the current page.
     * 
     * @return the page title
     */
    protected String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("[{}] Current page title: {}", pageName, title);
        return title;
    }

    /**
     * Gets the current URL of the page.
     * 
     * @return the current URL
     */
    protected String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.debug("[{}] Current URL: {}", pageName, url);
        return url;
    }

    /**
     * Waits for the page to fully load by checking document.readyState.
     * 
     * @param timeout the maximum time to wait in seconds
     */
    protected void waitForPageLoad(int timeout) {
        logger.debug("[{}] Waiting for page to load (timeout: {}s)", pageName, timeout);
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            customWait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
            logger.debug("[{}] Page load complete | URL: {}", pageName, driver.getCurrentUrl());
        } catch (TimeoutException e) {
            logger.warn("[{}] Page load timeout after {}s | URL: {}", pageName, timeout, driver.getCurrentUrl());
        }
    }

    /**
     * Waits for the page to fully load using the default timeout.
     */
    protected void waitForPageLoad() {
        waitForPageLoad(DEFAULT_TIMEOUT);
    }

    /**
     * Highlights a WebElement temporarily for debugging purposes.
     * 
     * @param element the WebElement to highlight
     */
    protected void highlightElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String originalStyle = element.getAttribute("style");
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", 
                    element, "border: 3px solid red; background: yellow;");
            Thread.sleep(500);
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", 
                    element, originalStyle);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.debug("[{}] Could not highlight element", pageName);
        }
    }

    /**
     * Highlights an element temporarily for debugging purposes using By locator.
     * 
     * @param locator the By locator to find the element
     */
    protected void highlightElement(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            highlightElement(element);
        } catch (Exception e) {
            logger.debug("[{}] Could not highlight element: {}", pageName, locator);
        }
    }

    /**
     * Waits for an element to be visible using FluentWait with custom polling.
     * More flexible than WebDriverWait - allows custom polling intervals and exception handling.
     *
     * @param locator the By locator to find the element
     * @param elementName descriptive name for logging
     * @param timeoutSeconds maximum time to wait in seconds
     * @param pollingMillis polling interval in milliseconds
     * @return the WebElement once it is visible
     */
    protected WebElement fluentWaitForElementVisible(By locator, String elementName, int timeoutSeconds, int pollingMillis) {
        logger.debug("[{}] FluentWait: Waiting for '{}' to be visible (timeout: {}s, polling: {}ms)",
                pageName, elementName, timeoutSeconds, pollingMillis);
        try {
            FluentWait<WebDriver> customFluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(timeoutSeconds))
                    .pollingEvery(Duration.ofMillis(pollingMillis))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);

            WebElement element = customFluentWait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    WebElement el = driver.findElement(locator);
                    return el.isDisplayed() ? el : null;
                }
            });

            logger.debug("[{}] FluentWait: '{}' is now visible", pageName, elementName);
            return element;
        } catch (Exception e) {
            logPageState();
            String errorMsg = String.format("[%s] FLUENT WAIT TIMEOUT: Element '%s' not visible within %d seconds. Current URL: %s",
                    pageName, elementName, timeoutSeconds, driver.getCurrentUrl());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Waits for an element to be visible using FluentWait with default settings.
     *
     * @param locator the By locator to find the element
     * @param elementName descriptive name for logging
     * @return the WebElement once it is visible
     */
    protected WebElement fluentWaitForElementVisible(By locator, String elementName) {
        return fluentWaitForElementVisible(locator, elementName, DEFAULT_TIMEOUT, 500);
    }

    /**
     * Waits for an element to be clickable using FluentWait.
     *
     * @param locator the By locator to find the element
     * @param elementName descriptive name for logging
     * @param timeoutSeconds maximum time to wait in seconds
     * @param pollingMillis polling interval in milliseconds
     * @return the WebElement once it is clickable
     */
    protected WebElement fluentWaitForElementClickable(By locator, String elementName, int timeoutSeconds, int pollingMillis) {
        logger.debug("[{}] FluentWait: Waiting for '{}' to be clickable (timeout: {}s, polling: {}ms)",
                pageName, elementName, timeoutSeconds, pollingMillis);
        try {
            FluentWait<WebDriver> customFluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(timeoutSeconds))
                    .pollingEvery(Duration.ofMillis(pollingMillis))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);

            WebElement element = customFluentWait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    WebElement el = driver.findElement(locator);
                    return (el.isDisplayed() && el.isEnabled()) ? el : null;
                }
            });

            logger.debug("[{}] FluentWait: '{}' is now clickable", pageName, elementName);
            return element;
        } catch (Exception e) {
            logPageState();
            String errorMsg = String.format("[%s] FLUENT WAIT TIMEOUT: Element '%s' not clickable within %d seconds. Current URL: %s",
                    pageName, elementName, timeoutSeconds, driver.getCurrentUrl());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Waits for an element to be clickable using FluentWait with default settings.
     *
     * @param locator the By locator to find the element
     * @param elementName descriptive name for logging
     * @return the WebElement once it is clickable
     */
    protected WebElement fluentWaitForElementClickable(By locator, String elementName) {
        return fluentWaitForElementClickable(locator, elementName, DEFAULT_TIMEOUT, 500);
    }

    /**
     * Waits for text to be present in an element using FluentWait.
     * Useful for dynamic content that changes over time.
     *
     * @param locator the By locator to find the element
     * @param expectedText the text to wait for
     * @param elementName descriptive name for logging
     * @param timeoutSeconds maximum time to wait in seconds
     * @param pollingMillis polling interval in milliseconds
     * @return true if text is found within timeout, false otherwise
     */
    protected boolean fluentWaitForText(By locator, String expectedText, String elementName, int timeoutSeconds, int pollingMillis) {
        logger.debug("[{}] FluentWait: Waiting for text '{}' in '{}' (timeout: {}s, polling: {}ms)",
                pageName, expectedText, elementName, timeoutSeconds, pollingMillis);
        try {
            FluentWait<WebDriver> customFluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(timeoutSeconds))
                    .pollingEvery(Duration.ofMillis(pollingMillis))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);

            Boolean result = customFluentWait.until(new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    try {
                        WebElement element = driver.findElement(locator);
                        String actualText = element.getText();
                        return actualText != null && actualText.contains(expectedText);
                    } catch (Exception e) {
                        return false;
                    }
                }
            });

            logger.debug("[{}] FluentWait: Text '{}' found in '{}': {}", pageName, expectedText, elementName, result);
            return result != null && result;
        } catch (Exception e) {
            logger.debug("[{}] FluentWait: Text '{}' not found in '{}' within timeout", pageName, expectedText, elementName);
            return false;
        }
    }

    /**
     * Waits for text to be present in an element using FluentWait with default settings.
     *
     * @param locator the By locator to find the element
     * @param expectedText the text to wait for
     * @param elementName descriptive name for logging
     * @return true if text is found within timeout, false otherwise
     */
    protected boolean fluentWaitForText(By locator, String expectedText, String elementName) {
        return fluentWaitForText(locator, expectedText, elementName, DEFAULT_TIMEOUT, 500);
    }

    /**
     * Waits for an element to disappear using FluentWait.
     *
     * @param locator the By locator to find the element
     * @param elementName descriptive name for logging
     * @param timeoutSeconds maximum time to wait in seconds
     * @param pollingMillis polling interval in milliseconds
     * @return true if element disappears within timeout, false otherwise
     */
    protected boolean fluentWaitForElementToDisappear(By locator, String elementName, int timeoutSeconds, int pollingMillis) {
        logger.debug("[{}] FluentWait: Waiting for '{}' to disappear (timeout: {}s, polling: {}ms)",
                pageName, elementName, timeoutSeconds, pollingMillis);
        try {
            FluentWait<WebDriver> customFluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(timeoutSeconds))
                    .pollingEvery(Duration.ofMillis(pollingMillis))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);

            Boolean result = customFluentWait.until(new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    try {
                        WebElement element = driver.findElement(locator);
                        return !element.isDisplayed();
                    } catch (NoSuchElementException e) {
                        return true; // Element not found means it disappeared
                    } catch (StaleElementReferenceException e) {
                        return true; // Element became stale means it disappeared
                    }
                }
            });

            logger.debug("[{}] FluentWait: '{}' disappeared: {}", pageName, elementName, result);
            return result != null && result;
        } catch (Exception e) {
            logger.debug("[{}] FluentWait: '{}' did not disappear within timeout", pageName, elementName);
            return false;
        }
    }

    /**
     * Waits for an element to disappear using FluentWait with default settings.
     *
     * @param locator the By locator to find the element
     * @param elementName descriptive name for logging
     * @return true if element disappears within timeout, false otherwise
     */
    protected boolean fluentWaitForElementToDisappear(By locator, String elementName) {
        return fluentWaitForElementToDisappear(locator, elementName, DEFAULT_TIMEOUT, 500);
    }

    /**
     * Takes a screenshot and logs the current page state for debugging.
     * Call this method when you need to capture the state during failures.
     */
    protected void captureDebugInfo() {
        logger.info("========== DEBUG INFO ==========");
        logger.info("[{}] URL: {}", pageName, driver.getCurrentUrl());
        logger.info("[{}] Title: {}", pageName, driver.getTitle());
        try {
            String pageSource = driver.getPageSource();
            logger.debug("[{}] Page source length: {} characters", pageName, pageSource.length());
        } catch (Exception e) {
            logger.warn("[{}] Could not capture page source: {}", pageName, e.getMessage());
        }
        logger.info("================================");
    }
}
