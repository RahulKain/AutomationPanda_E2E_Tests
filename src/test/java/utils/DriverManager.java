package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * DriverManager - Thread-safe WebDriver management utility class.
 * Uses ThreadLocal to ensure each thread has its own WebDriver instance,
 * enabling safe parallel test execution.
 * 
 * <p>This class follows the utility class pattern with a private constructor
 * and static methods for WebDriver lifecycle management.</p>
 * 
 * @author AutomationPanda
 * @version 1.0
 */
public final class DriverManager {

    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    
    /**
     * ThreadLocal storage for WebDriver instances.
     * Each thread gets its own isolated WebDriver instance.
     */
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private DriverManager() {
        throw new UnsupportedOperationException("DriverManager is a utility class and cannot be instantiated");
    }

    /**
     * Gets the WebDriver instance for the current thread.
     * If no driver exists for the current thread, a new one is created
     * based on the browser configuration.
     * 
     * @return the WebDriver instance for the current thread
     * @throws IllegalArgumentException if an unsupported browser is specified
     */
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            logger.info("Creating new WebDriver instance for thread: {}", Thread.currentThread().getName());
            WebDriver driver = createDriver();
            driverThreadLocal.set(driver);
            configureDriver(driver);
        }
        return driverThreadLocal.get();
    }

    /**
     * Creates a new WebDriver instance based on the browser configuration.
     * Supports Chrome, Firefox, and Edge browsers with optional headless mode.
     * 
     * @return a new WebDriver instance
     * @throws IllegalArgumentException if an unsupported browser is specified
     */
    private static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        boolean headless = ConfigReader.isHeadless();
        
        logger.info("Initializing {} browser (headless: {})", browser, headless);
        
        return switch (browser) {
            case "chrome" -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            case "edge" -> createEdgeDriver(headless);
            default -> {
                logger.error("Unsupported browser: {}", browser);
                throw new IllegalArgumentException("Unsupported browser: " + browser + 
                        ". Supported browsers are: chrome, firefox, edge");
            }
        };
    }

    /**
     * Creates a Chrome WebDriver instance.
     * 
     * @param headless whether to run in headless mode
     * @return a new ChromeDriver instance
     */
    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        
        // Common Chrome options for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        
        logger.debug("Chrome options configured: {}", options.asMap());
        return new ChromeDriver(options);
    }

    /**
     * Creates a Firefox WebDriver instance.
     * 
     * @param headless whether to run in headless mode
     * @return a new FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("-headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        
        logger.debug("Firefox options configured: {}", options.asMap());
        return new FirefoxDriver(options);
    }

    /**
     * Creates an Edge WebDriver instance.
     * 
     * @param headless whether to run in headless mode
     * @return a new EdgeDriver instance
     */
    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        
        // Common Edge options for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        logger.debug("Edge options configured: {}", options.asMap());
        return new EdgeDriver(options);
    }

    /**
     * Configures the WebDriver with common settings.
     * Applies implicit wait and maximizes the browser window.
     * 
     * @param driver the WebDriver instance to configure
     */
    private static void configureDriver(WebDriver driver) {
        int implicitWait = ConfigReader.getImplicitWait();
        logger.info("Configuring driver with implicit wait: {} seconds", implicitWait);
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().window().maximize();
        
        logger.info("WebDriver configured successfully");
    }

    /**
     * Quits the WebDriver instance for the current thread and removes it from ThreadLocal.
     * This method should be called after each test scenario to clean up resources.
     * Safe to call even if no driver exists for the current thread.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            logger.info("Quitting WebDriver for thread: {}", Thread.currentThread().getName());
            try {
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.error("Error while quitting WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
                logger.debug("WebDriver removed from ThreadLocal");
            }
        } else {
            logger.debug("No WebDriver to quit for thread: {}", Thread.currentThread().getName());
        }
    }

    /**
     * Checks if a WebDriver instance exists for the current thread.
     * 
     * @return true if a WebDriver exists for the current thread, false otherwise
     */
    public static boolean hasDriver() {
        return driverThreadLocal.get() != null;
    }
}
