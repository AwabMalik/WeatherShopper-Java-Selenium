package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * CheckoutPage - Represents the checkout page after cart
 * This page may appear after clicking "Pay with Card" and before payment form
 */
public class CheckoutPage extends BasePage {

    // Locators
    private By checkoutHeading = By.xpath("//h2[contains(text(),'Checkout')]");
    private By proceedButton = By.xpath("//button[contains(text(),'Proceed')]");

    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Verify Checkout page is loaded
     * @return true if checkout heading is visible
     */
    public boolean isCheckoutPageLoaded() {
        try {
            return isDisplayed(checkoutHeading);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get checkout page heading
     * @return Heading text
     */
    public String getCheckoutHeading() {
        return getText(checkoutHeading);
    }

    /**
     * Click proceed button if present
     */
    public void clickProceed() {
        if (isDisplayed(proceedButton)) {
            click(proceedButton);
            waitForPageLoad();
        }
    }
}
