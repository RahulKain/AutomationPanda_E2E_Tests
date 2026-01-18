package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader - Utility class for reading configuration properties.
 * Uses static initialization to load properties once at class loading time.
 * Supports system property overrides for CI/CD flexibility.
 */
public class ConfigReader {

    private static Properties properties;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    // Static block to load properties when class is first accessed
    static {
        loadProperties();
    }

    /**
     * Loads properties from the config.properties file.
     * Throws RuntimeException if file cannot be loaded.
     */
    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties file from path: " 
                    + CONFIG_PATH + " - " + e.getMessage());
        }
    }

    /**
     * Gets a property value by key.
     * First checks system properties for environment override, then falls back to config file.
     * 
     * @param key the property key
     * @return the property value, or null if not found
     */
    public static String getProperty(String key) {
        // First check system properties for environment override (e.g., -Dbrowser=firefox)
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        return properties.getProperty(key);
    }

    /**
     * Gets a property value by key with a default value fallback.
     * 
     * @param key the property key
     * @param defaultValue the default value if property is not found
     * @return the property value, or defaultValue if not found
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    // ==================== Convenience Methods ====================

    /**
     * Gets the application URL from config.
     * 
     * @return the URL string
     */
    public static String getUrl() {
        return getProperty("url");
    }

    /**
     * Gets the browser name from config.
     * Defaults to "chrome" if not specified.
     * 
     * @return the browser name (chrome, firefox, edge)
     */
    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }

    /**
     * Gets the implicit wait timeout in seconds.
     * Defaults to 10 seconds if not specified.
     * 
     * @return implicit wait timeout in seconds
     */
    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait", "10"));
    }

    /**
     * Gets the explicit wait timeout in seconds.
     * Defaults to 20 seconds if not specified.
     * 
     * @return explicit wait timeout in seconds
     */
    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait", "20"));
    }

    /**
     * Checks if browser should run in headless mode.
     * Defaults to false if not specified.
     * 
     * @return true if headless mode is enabled
     */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    /**
     * Checks if screenshots should be taken on test failure.
     * Defaults to true if not specified.
     * 
     * @return true if screenshot on failure is enabled
     */
    public static boolean takeScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }
}
