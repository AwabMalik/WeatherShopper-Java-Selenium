package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

/**
 * BasePage class containing common methods and utilities for all page objects
 * Provides reusable WebDriver operations with explicit waits
 */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT = 15;

    /**
     * Constructor to initialize WebDriver and WebDriverWait
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    /**
     * Wait for element to be visible
     * @param locator By locator for the element
     * @return WebElement once visible
     */
    protected WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be clickable
     * @param locator By locator for the element
     * @return WebElement once clickable
     */
    protected WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for all elements to be visible
     * @param locator By locator for the elements
     * @return List of WebElements once visible
     */
    protected List<WebElement> waitForElementsVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /**
     * Click on element with explicit wait
     * @param locator By locator for the element
     */
    protected void click(By locator) {
        waitForElementClickable(locator).click();
    }

    /**
     * Click on WebElement directly
     * @param element WebElement to click
     */
    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /**
     * Type text into input field
     * @param locator By locator for the element
     * @param text Text to type
     */
    protected void sendKeys(By locator, String text) {
        WebElement element = waitForElementVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Get text from element
     * @param locator By locator for the element
     * @return Text content of the element
     */
    protected String getText(By locator) {
        return waitForElementVisible(locator).getText();
    }

    /**
     * Get text from WebElement
     * @param element WebElement
     * @return Text content of the element
     */
    protected String getText(WebElement element) {
        return element.getText();
    }

    /**
     * Check if element is displayed
     * @param locator By locator for the element
     * @return true if displayed, false otherwise
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Get all elements matching the locator
     * @param locator By locator for the elements
     * @return List of WebElements
     */
    protected List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    /**
     * Scroll element into view
     * @param element WebElement to scroll to
     */
    protected void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Wait for page to load completely
     */
    protected void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Get current page URL
     * @return Current URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get page title
     * @return Page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Switch to iframe
     * @param locator By locator for the iframe
     */
    protected void switchToFrame(By locator) {
        WebElement frame = waitForElementVisible(locator);
        driver.switchTo().frame(frame);
    }

    /**
     * Switch to default content (exit iframe)
     */
    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    /**
     * Accept alert if present
     */
    protected void acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    /**
     * Dismiss alert if present
     */
    protected void dismissAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).dismiss();
    }

    /**
     * Get alert text
     * @return Alert text
     */
    protected String getAlertText() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }
}
