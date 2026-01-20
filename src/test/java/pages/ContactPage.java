package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContactPage - Page Object for the Automation Panda contact page.
 * Represents the contact page with contact form functionality.
 *
 * <p>This page contains the contact form with name, email, and message fields.</p>
 * <p>Uses Selenium PageFactory pattern with @FindBy annotations.</p>
 * <p>Enhanced with detailed error tracking and logging for better debugging.</p>
 *
 * @author AutomationPanda
 * @version 1.0
 */
public class ContactPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(ContactPage.class);

    // ==================== Element Names for Logging ====================
    private static final String CONTACT_FORM_NAME = "Contact Form";
    private static final String NAME_FIELD_NAME = "Name Input Field";
    private static final String EMAIL_FIELD_NAME = "Email Input Field";
    private static final String MESSAGE_FIELD_NAME = "Message Textarea";
    private static final String SUBMIT_BUTTON_NAME = "Submit Button";
    private static final String SUCCESS_MESSAGE_NAME = "Success Message";
    private static final String ERROR_MESSAGE_NAME = "Error Message";

    // ==================== Page Elements using @FindBy ====================

    /** Contact form container */
    @FindBy(css = "form.contact-form, form.wpcf7-form, #contact-form")
    private WebElement contactForm;

    /** Name input field */
    @FindBy(css = "input[name*='name'], input[id*='name'], input[placeholder*='name'], input[placeholder*='Name']")
    private WebElement nameField;

    /** Email input field */
    @FindBy(css = "input[name*='email'], input[id*='email'], input[type='email'], input[placeholder*='email'], input[placeholder*='Email']")
    private WebElement emailField;

    /** Message textarea */
    @FindBy(css = "textarea[name*='message'], textarea[id*='message'], textarea[placeholder*='message'], textarea[placeholder*='Message']")
    private WebElement messageField;

    /** Submit button */
    @FindBy(xpath = "//button[.//strong[text()='Contact Me']]")
    private WebElement submitButton;

    /** Success message element */
    @FindBy(id = "contact-form-success-header")
    private WebElement successMessage;

    /** Error message elements */
    @FindBy(css = ".contact-form__error.show-errors")
    private WebElement errorMessage;

    // ==================== By Locators for dynamic elements ====================

    /** Name field locator for dynamic waiting */
    private static final By NAME_INPUT = By.cssSelector("input[name*='name'], input[id*='name'], input[placeholder*='name'], input[placeholder*='Name']");

    /** Email field locator for dynamic waiting */
    private static final By EMAIL_INPUT = By.cssSelector("input[name*='email'], input[id*='email'], input[type='email'], input[placeholder*='email'], input[placeholder*='Email']");

    /** Message field locator for dynamic waiting */
    private static final By MESSAGE_TEXTAREA = By.cssSelector("textarea[name*='message'], textarea[id*='message'], textarea[placeholder*='message'], textarea[placeholder*='Message']");

    /** Submit button locator for dynamic waiting */
    private static final By SUBMIT_BUTTON = By.xpath("//button[.//strong[text()='Contact Me']]");

    // ==================== Constructor ====================

    /**
     * Constructor that initializes the ContactPage with a WebDriver instance.
     * PageFactory.initElements is called in the parent BasePage constructor.
     *
     * @param driver the WebDriver instance to use for browser interactions
     */
    public ContactPage(WebDriver driver) {
        super(driver);
        logger.info("[ContactPage] Page object created for URL: {}", driver.getCurrentUrl());
    }

    // ==================== Page Methods ====================

    /**
     * Verifies that the contact page is loaded by checking for the contact form.
     *
     * @return true if the contact page is loaded, false otherwise
     */
    public boolean isContactPageLoaded() {
        logger.info("[ContactPage] Verifying contact page is loaded...");
        try {
            waitForPageLoad();

            // Check if we're on the contact page by URL
            String currentUrl = driver.getCurrentUrl();
            boolean isContactPage = currentUrl != null && currentUrl.contains("contact");

            // Check for form elements (be more lenient)
            boolean formPresent = isDisplayed(contactForm, CONTACT_FORM_NAME);
            boolean nameFieldPresent = isDisplayed(nameField, NAME_FIELD_NAME);
            boolean emailFieldPresent = isDisplayed(emailField, EMAIL_FIELD_NAME);
            boolean messageFieldPresent = isDisplayed(messageField, MESSAGE_FIELD_NAME);
            boolean submitButtonPresent = isDisplayed(submitButton, SUBMIT_BUTTON_NAME);

            // Consider page loaded if we're on contact URL and have at least some form elements
            boolean isLoaded = isContactPage && (formPresent || (nameFieldPresent && emailFieldPresent));

            if (isLoaded) {
                logger.info("[ContactPage] VERIFIED: Contact page loaded successfully | URL: {} | Form: {} | Name: {} | Email: {} | Message: {} | Submit: {}",
                           isContactPage, formPresent, nameFieldPresent, emailFieldPresent, messageFieldPresent, submitButtonPresent);
            } else {
                logger.warn("[ContactPage] VERIFICATION FAILED: Contact page may not be fully loaded | URL: {} | Form: {} | Name: {} | Email: {} | Message: {} | Submit: {}",
                           isContactPage, formPresent, nameFieldPresent, emailFieldPresent, messageFieldPresent, submitButtonPresent);
                captureDebugInfo();
            }
            return isLoaded;
        } catch (Exception e) {
            logger.error("[ContactPage] ERROR checking if contact page is loaded: {}", e.getMessage());
            captureDebugInfo();
            return false;
        }
    }

    /**
     * Enters the name in the contact form.
     *
     * @param name the name to enter
     */
    public void enterName(String name) {
        logger.info("[ContactPage] Entering name: '{}'", name);
        try {
            WebElement nameInput = fluentWaitForElementVisible(NAME_INPUT, NAME_FIELD_NAME, 10, 500);
            nameInput.clear();
            nameInput.sendKeys(name);
            logger.info("[ContactPage] SUCCESS: Name entered: '{}'", name);
        } catch (Exception e) {
            logger.error("[ContactPage] FAILED: Could not enter name '{}': {}", name, e.getMessage());
            captureDebugInfo();
            throw new RuntimeException("Could not enter name: " + name, e);
        }
    }

    /**
     * Enters the email in the contact form.
     *
     * @param email the email to enter
     */
    public void enterEmail(String email) {
        logger.info("[ContactPage] Entering email: '{}'", email);
        try {
            WebElement emailInput = fluentWaitForElementVisible(EMAIL_INPUT, EMAIL_FIELD_NAME, 10, 500);
            emailInput.clear();
            emailInput.sendKeys(email);
            logger.info("[ContactPage] SUCCESS: Email entered: '{}'", email);
        } catch (Exception e) {
            logger.error("[ContactPage] FAILED: Could not enter email '{}': {}", email, e.getMessage());
            captureDebugInfo();
            throw new RuntimeException("Could not enter email: " + email, e);
        }
    }

    /**
     * Enters the message in the contact form.
     *
     * @param message the message to enter
     */
    public void enterMessage(String message) {
        logger.info("[ContactPage] Entering message: '{}'", message);
        try {
            WebElement messageTextarea = fluentWaitForElementVisible(MESSAGE_TEXTAREA, MESSAGE_FIELD_NAME, 10, 500);
            messageTextarea.clear();
            messageTextarea.sendKeys(message);
            logger.info("[ContactPage] SUCCESS: Message entered");
        } catch (Exception e) {
            logger.error("[ContactPage] FAILED: Could not enter message: {}", e.getMessage());
            captureDebugInfo();
            throw new RuntimeException("Could not enter message", e);
        }
    }

    /**
     * Clicks the submit button on the contact form.
     */
    public void submitForm() {
        logger.info("[ContactPage] ========== FORM SUBMISSION ==========");
        logger.info("[ContactPage] Clicking submit button...");
        try {
            // Wait for the button to be clickable (visible and enabled)
            WebElement submitBtn = fluentWaitForElementClickable(SUBMIT_BUTTON, SUBMIT_BUTTON_NAME, 20, 500);

            submitBtn.click();
            logger.info("[ContactPage] SUCCESS: Form submitted");

            logger.info("[ContactPage] ===================================");
        } catch (Exception e) {
            logger.error("[ContactPage] FAILED: Could not submit form: {}", e.getMessage());
            captureDebugInfo();
            throw new RuntimeException("Could not submit form", e);
        }
    }

    /**
     * Fills out and submits the contact form with the provided details.
     *
     * @param name the name to enter
     * @param email the email to enter
     * @param message the message to enter
     */
    public void fillAndSubmitContactForm(String name, String email, String message) {
        logger.info("[ContactPage] ========== FILL AND SUBMIT FORM ==========");
        logger.info("[ContactPage] Filling contact form with name: '{}', email: '{}'", name, email);

        enterName(name);
        enterEmail(email);
        enterMessage(message);
        submitForm();

        logger.info("[ContactPage] ============================================");
    }

    /**
     * Checks if a success message is displayed after form submission.
     *
     * @return true if success message is visible, false otherwise
     */
    public boolean isSuccessMessageDisplayed() {
        logger.info("[ContactPage] Checking for success message...");
        try {
            boolean displayed = isDisplayed(successMessage, SUCCESS_MESSAGE_NAME);
            logger.info("[ContactPage] Success message displayed: {}", displayed);
            return displayed;
        } catch (Exception e) {
            logger.debug("[ContactPage] Success message not found: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an error message is displayed (for validation errors).
     *
     * @return true if error message is visible, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        logger.info("[ContactPage] Checking for error message...");
        try {
            boolean displayed = isDisplayed(errorMessage, ERROR_MESSAGE_NAME);
            logger.info("[ContactPage] Error message displayed: {}", displayed);
            return displayed;
        } catch (Exception e) {
            logger.debug("[ContactPage] Error message not found: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the text of the success message.
     *
     * @return the success message text, or empty string if not found
     */
    public String getSuccessMessageText() {
        logger.info("[ContactPage] Getting success message text...");
        try {
            String text = getText(successMessage, SUCCESS_MESSAGE_NAME);
            logger.info("[ContactPage] Success message: '{}'", text);
            return text;
        } catch (Exception e) {
            logger.debug("[ContactPage] Could not get success message: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Gets the text of the error message.
     *
     * @return the error message text, or empty string if not found
     */
    public String getErrorMessageText() {
        logger.info("[ContactPage] Getting error message text...");
        try {
            String text = getText(errorMessage, ERROR_MESSAGE_NAME);
            logger.info("[ContactPage] Error message: '{}'", text);
            return text;
        } catch (Exception e) {
            logger.debug("[ContactPage] Could not get error message: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Checks if the email field has a validation error.
     *
     * @return true if email field shows validation error, false otherwise
     */
    public boolean isEmailValidationErrorDisplayed() {
        logger.info("[ContactPage] Checking for email validation error...");
        try {
            // Look for validation error specifically on email field
            By emailErrorLocator = By.cssSelector("input[name*='email'], input[id*='email'], input[type='email'], input[placeholder*='email'], input[placeholder*='Email']");
            WebElement emailInput = driver.findElement(emailErrorLocator);
            String classes = emailInput.getAttribute("class");
            boolean hasError = classes != null && (classes.contains("wpcf7-not-valid") || classes.contains("error") || classes.contains("invalid"));
            logger.info("[ContactPage] Email validation error: {}", hasError);
            return hasError;
        } catch (Exception e) {
            logger.debug("[ContactPage] Could not check email validation: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the message field has a validation error.
     *
     * @return true if message field shows validation error, false otherwise
     */
    public boolean isMessageValidationErrorDisplayed() {
        logger.info("[ContactPage] Checking for message validation error...");
        try {
            // Look for validation error specifically on message field
            By messageErrorLocator = By.cssSelector("textarea[name*='message'], textarea[id*='message'], textarea[placeholder*='message'], textarea[placeholder*='Message']");
            WebElement messageTextarea = driver.findElement(messageErrorLocator);
            String classes = messageTextarea.getAttribute("class");
            boolean hasError = classes != null && (classes.contains("wpcf7-not-valid") || classes.contains("error") || classes.contains("invalid"));
            logger.info("[ContactPage] Message validation error: {}", hasError);
            return hasError;
        } catch (Exception e) {
            logger.debug("[ContactPage] Could not check message validation: {}", e.getMessage());
            return false;
        }
    }
}