package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;

/**
 * SunscreensPage - Represents the Sunscreens shopping page
 * Handles selection of least expensive SPF-50 and SPF-30 sunscreens
 */
public class SunscreensPage extends BasePage {

    // Locators
    private By pageHeading = By.xpath("//h2[contains(text(),'Sunscreens')]");
    private By allProducts = By.xpath("//button[contains(text(),'Add')]/parent::*");
    private By cartButton = By.xpath("//button[@onclick='goToCart()']");
    private By cartBadge = By.id("cart");

    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public SunscreensPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Verify Sunscreens page is loaded
     * @return true if page heading is visible
     */
    public boolean isSunscreensPageLoaded() {
        try {
            return isDisplayed(pageHeading);
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
     * Product class to store product details
     */
    private static class Product {
        WebElement element;
        String name;
        int price;
        WebElement addButton;

        Product(WebElement element, String name, int price, WebElement addButton) {
            this.element = element;
            this.name = name;
            this.price = price;
            this.addButton = addButton;
        }
    }

    /**
     * Get all products on the page
     * @return List of Product objects
     */
    private List<Product> getAllProducts() {
        // Wait for page to load
        waitForPageLoad();

        // Try multiple locator strategies
        List<WebElement> productElements = null;
        try {
            productElements = waitForElementsVisible(allProducts);
        } catch (Exception e) {
            System.out.println("Using fallback locator strategy...");
            // Fallback: find by Add buttons and get parent containers
            List<WebElement> addButtons = driver.findElements(By.xpath("//button[contains(text(),'Add')]"));
            productElements = new ArrayList<>();
            for (WebElement btn : addButtons) {
                productElements.add(btn.findElement(By.xpath("./..")));
            }
        }

        List<Product> products = new ArrayList<>();

        for (WebElement productElement : productElements) {
            try {
                // Try multiple strategies to find product name
                String name = "";
                try {
                    name = productElement.findElement(By.xpath(".//p[@class='font-weight-bold top-space-10']")).getText();
                } catch (Exception e1) {
                    try {
                        name = productElement.findElement(By.xpath(".//p[contains(@class,'font-weight-bold')]")).getText();
                    } catch (Exception e2) {
                        // Find first p tag that doesn't contain "Price"
                        List<WebElement> pTags = productElement.findElements(By.xpath(".//p"));
                        for (WebElement p : pTags) {
                            String text = p.getText();
                            if (!text.contains("Price") && !text.isEmpty()) {
                                name = text;
                                break;
                            }
                        }
                    }
                }

                // Try to find price
                String priceText = productElement.findElement(By.xpath(".//p[contains(text(),'Price:')]")).getText();
                // Extract price number from "Price: Rs. 500" format
                int price = Integer.parseInt(priceText.replaceAll("[^0-9]", ""));

                // Find Add button
                WebElement addButton = productElement.findElement(By.xpath(".//button[contains(text(),'Add')]"));

                products.add(new Product(productElement, name, price, addButton));
                System.out.println("Found product: " + name + " - Rs. " + price);
            } catch (Exception e) {
                System.out.println("Error parsing product: " + e.getMessage());
            }
        }
        return products;
    }

    /**
     * Find the least expensive product containing a specific SPF level
     * @param spfLevel SPF level to search for (e.g., "SPF-50", "SPF-30")
     * @return Product object of the least expensive matching product
     */
    private Product findLeastExpensiveProduct(String spfLevel) {
        List<Product> allProducts = getAllProducts();
        Product leastExpensive = null;

        for (Product product : allProducts) {
            // Check if product name contains the SPF level (case-insensitive)
            if (product.name.toLowerCase().contains(spfLevel.toLowerCase())) {
                if (leastExpensive == null || product.price < leastExpensive.price) {
                    leastExpensive = product;
                }
            }
        }

        if (leastExpensive == null) {
            throw new RuntimeException("No product found with SPF level: " + spfLevel);
        }

        System.out.println("Found least expensive " + spfLevel + " product: " +
            leastExpensive.name + " - Rs. " + leastExpensive.price);
        return leastExpensive;
    }

    /**
     * Add the least expensive sunscreen with SPF-50 to cart
     */
    public void addLeastExpensiveSPF50() {
        Product spf50Product = findLeastExpensiveProduct("SPF-50");
        scrollToElement(spf50Product.addButton);
        click(spf50Product.addButton);
        System.out.println("Added to cart: " + spf50Product.name);
    }

    /**
     * Add the least expensive sunscreen with SPF-30 to cart
     */
    public void addLeastExpensiveSPF30() {
        Product spf30Product = findLeastExpensiveProduct("SPF-30");
        scrollToElement(spf30Product.addButton);
        click(spf30Product.addButton);
        System.out.println("Added to cart: " + spf30Product.name);
    }

    /**
     * Add both required sunscreens (SPF-50 and SPF-30) to cart
     */
    public void addRequiredSunscreens() {
        addLeastExpensiveSPF50();
        addLeastExpensiveSPF30();
    }

    /**
     * Get cart badge count
     * @return Number of items in cart
     */
    public int getCartCount() {
        try {
            String cartText = getText(cartBadge);
            // Extract number from "Cart - 2" format
            String numericText = cartText.replaceAll("[^0-9]", "");
            if (numericText.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(numericText);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Verify cart count matches expected value
     * @param expectedCount Expected number of items in cart
     * @return true if cart count matches expected value
     */
    public boolean verifyCartCount(int expectedCount) {
        int actualCount = getCartCount();
        System.out.println("Cart count - Expected: " + expectedCount + ", Actual: " + actualCount);
        return actualCount == expectedCount;
    }

    /**
     * Click on Cart button to proceed to cart page
     */
    public void clickCartButton() {
        click(cartButton);
        waitForPageLoad();
        System.out.println("Navigated to Cart page");
    }
}
