package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

/**
 * CartPage - Represents the shopping cart page
 * Handles cart verification and proceeding to checkout
 */
public class CartPage extends BasePage {

    // Locators
    private By pageHeading = By.xpath("//h2[contains(text(),'Checkout')]");
    private By cartTable = By.xpath("//table[@class='table table-striped']");
    private By cartItems = By.xpath("//table[@class='table table-striped']//tbody//tr");
    private By totalPrice = By.id("total");
    // Multiple locator strategies for Pay with Card button
    private By payWithCardButton = By.xpath("//button[contains(text(),'Pay with Card')]");
    private By payWithCardButtonAlt1 = By.xpath("//button[@class='stripe-button-el']");
    private By payWithCardButtonAlt2 = By.xpath("//button[contains(@class,'stripe')]");
    private By payWithCardButtonAlt3 = By.xpath("//span[contains(text(),'Pay with Card')]/parent::button");

    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public CartPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Verify Cart/Checkout page is loaded
     * @return true if page heading is visible
     */
    public boolean isCartPageLoaded() {
        try {
            return isDisplayed(pageHeading) && isDisplayed(cartTable);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get page heading text
     * @return Page heading text
     */
    public String getPageHeading() {
        return getText(pageHeading);
    }

    /**
     * Get all items in the cart
     * @return List of cart item WebElements
     */
    public List<WebElement> getCartItems() {
        return waitForElementsVisible(cartItems);
    }

    /**
     * Get count of items in cart
     * @return Number of items in cart
     */
    public int getCartItemCount() {
        return getCartItems().size();
    }

    /**
     * Verify cart has expected number of items
     * @param expectedCount Expected number of items
     * @return true if cart item count matches expected count
     */
    public boolean verifyCartItemCount(int expectedCount) {
        int actualCount = getCartItemCount();
        System.out.println("Cart items - Expected: " + expectedCount + ", Actual: " + actualCount);
        return actualCount == expectedCount;
    }

    /**
     * Get cart item details (name and price)
     * @param index Index of the cart item (0-based)
     * @return String array with [name, price]
     */
    public String[] getCartItemDetails(int index) {
        List<WebElement> items = getCartItems();
        if (index < 0 || index >= items.size()) {
            throw new IndexOutOfBoundsException("Invalid cart item index: " + index);
        }

        WebElement item = items.get(index);
        String name = item.findElement(By.xpath(".//td[1]")).getText();
        String price = item.findElement(By.xpath(".//td[2]")).getText();

        return new String[]{name, price};
    }

    /**
     * Print all cart items
     */
    public void printAllCartItems() {
        List<WebElement> items = getCartItems();
        System.out.println("Cart contains " + items.size() + " items:");
        for (int i = 0; i < items.size(); i++) {
            String[] details = getCartItemDetails(i);
            System.out.println((i + 1) + ". " + details[0] + " - " + details[1]);
        }
    }

    /**
     * Get total price from cart
     * @return Total price as string (e.g., "Rupees 1000")
     */
    public String getTotalPrice() {
        return getText(totalPrice);
    }

    /**
     * Get numeric value of total price
     * @return Total price as integer
     */
    public int getTotalPriceValue() {
        String priceText = getTotalPrice();
        // Extract numeric value from "Rupees 1000" or "Rs. 1000"
        String numericPrice = priceText.replaceAll("[^0-9]", "");
        return Integer.parseInt(numericPrice);
    }

    /**
     * Verify total price is displayed
     * @return true if total price is visible
     */
    public boolean isTotalPriceDisplayed() {
        return isDisplayed(totalPrice);
    }

    /**
     * Verify total price is greater than zero
     * @return true if total price > 0
     */
    public boolean isTotalPriceValid() {
        int price = getTotalPriceValue();
        boolean isValid = price > 0;
        System.out.println("Total price: " + price + " - Valid: " + isValid);
        return isValid;
    }

    /**
     * Click on "Pay with Card" button to proceed to payment
     */
    public void clickPayWithCard() {
        driver.findElement(By.xpath("//span[normalize-space()='Pay with Card']"));
        WebElement button;

        // Try multiple locator strategies
        try {
            button = waitForElementClickable(payWithCardButton);
            System.out.println("Found Pay button using primary locator");
        } catch (Exception e1) {
            System.out.println("Primary locator failed, trying alternative locators...");
            try {
                button = waitForElementClickable(payWithCardButtonAlt1);
                System.out.println("Found Pay button using stripe-button-el class");
            } catch (Exception e2) {
                try {
                    button = waitForElementClickable(payWithCardButtonAlt2);
                    System.out.println("Found Pay button using stripe class");
                } catch (Exception e3) {
                    try {
                        button = waitForElementClickable(payWithCardButtonAlt3);
                        System.out.println("Found Pay button using span parent strategy");
                    } catch (Exception e4) {
                        throw new RuntimeException("Could not find Pay with Card button using any locator strategy");
                    }
                }
            }
        }

        scrollToElement(button);
        click(button);
        System.out.println("Clicked 'Pay with Card' button");
        // Wait a moment for payment modal/iframe to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verify "Pay with Card" button is displayed
     * @return true if button is visible
     */
    public boolean isPayWithCardButtonDisplayed() {
        // Try multiple locator strategies
        if (isDisplayed(payWithCardButton)) {
            return true;
        }
        try {
            if (driver.findElements(payWithCardButtonAlt1).size() > 0) {
                return driver.findElement(payWithCardButtonAlt1).isDisplayed();
            }
        } catch (Exception e) {}

        try {
            if (driver.findElements(payWithCardButtonAlt2).size() > 0) {
                return driver.findElement(payWithCardButtonAlt2).isDisplayed();
            }
        } catch (Exception e) {}

        try {
            if (driver.findElements(payWithCardButtonAlt3).size() > 0) {
                return driver.findElement(payWithCardButtonAlt3).isDisplayed();
            }
        } catch (Exception e) {}

        return false;
    }
}
