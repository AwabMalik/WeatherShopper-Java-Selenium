package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * HomePage - Represents the Weather Shopper home page
 * Handles temperature reading and navigation to appropriate shopping page
 */
public class HomePage extends BasePage {

    // Locators
    private By temperatureLocator = By.id("temperature");
    private By moisturizersButton = By.xpath("//button[contains(text(),'Buy moisturizers')]");
    private By sunscreensButton = By.xpath("//button[contains(text(),'Buy sunscreens')]");
    private By infoIcon = By.xpath("//*[@class='octicon octicon-info']");
    private By taskInstructionsPopup = By.xpath("//h2[contains(text(),'Task')]");

    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate to Weather Shopper homepage
     * @param url Application URL
     */
    public void navigateToHomePage(String url) {
        driver.get(url);
        waitForPageLoad();
    }

    /**
     * Hover over information icon to view task
     */
    public void hoverOverInfoIcon() {
        WebElement infoElement = waitForElementVisible(infoIcon);
        org.openqa.selenium.interactions.Actions actions =
            new org.openqa.selenium.interactions.Actions(driver);
        actions.moveToElement(infoElement).perform();
    }

    /**
     * Click on information icon to view task details
     */
    public void clickInfoIcon() {
        click(infoIcon);
    }

    /**
     * Verify task instructions popup is displayed
     * @return true if task instructions popup is visible
     */
    public boolean isTaskInstructionsVisible() {
        try {
            return isDisplayed(taskInstructionsPopup);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get current temperature from the homepage
     * @return Temperature as integer (extracts numeric value)
     */
    public int getTemperature() {
        String tempText = getText(temperatureLocator).trim();
        // Extract numeric value from text like "25°C" or "15°C"
        String numericTemp = tempText.replaceAll("[^0-9-]", "");
        return Integer.parseInt(numericTemp);
    }

    /**
     * Get temperature text as displayed on page
     * @return Temperature text (e.g., "25°C")
     */
    public String getTemperatureText() {
        return getText(temperatureLocator);
    }

    /**
     * Navigate to appropriate shopping page based on temperature
     * If temperature < 19°C: Navigate to Moisturizers
     * If temperature > 34°C: Navigate to Sunscreens
     * @return String indicating which page was navigated to
     */
    public String navigateBasedOnTemperature() {
        int temperature = getTemperature();
        System.out.println("Current temperature: " + temperature + "°C");

        if (temperature <= 19) {
            System.out.println("Temperature is below 19°C. Navigating to Moisturizers page...");
            click(moisturizersButton);
            return "moisturizers";
        } else if (temperature >= 34) {
            System.out.println("Temperature is above 34°C. Navigating to Sunscreens page...");
            click(sunscreensButton);
            return "sunscreens";
        } else {
            throw new RuntimeException("Temperature " + temperature +
                "°C is not in valid range (should be <19 or >34)");
        }
    }

    /**
     * Click on Buy Moisturizers button
     */
    public void clickBuyMoisturizers() {
        click(moisturizersButton);
        waitForPageLoad();
    }

    /**
     * Click on Buy Sunscreens button
     */
    public void clickBuySunscreens() {
        click(sunscreensButton);
        waitForPageLoad();
    }

    /**
     * Verify Moisturizers button is displayed
     * @return true if button is visible
     */
    public boolean isMoisturizersButtonDisplayed() {
        return isDisplayed(moisturizersButton);
    }

    /**
     * Verify Sunscreens button is displayed
     * @return true if button is visible
     */
    public boolean isSunscreensButtonDisplayed() {
        return isDisplayed(sunscreensButton);
    }
}
