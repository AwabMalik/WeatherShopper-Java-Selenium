package tests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;
import utils.DriverManager;

/**
 * WeatherShopperTest - End-to-end test for Weather Shopper application
 * Tests the complete flow from homepage to payment based on temperature
 */
public class WeatherShopperTest {

    private WebDriver driver;
    private HomePage homePage;
    private MoisturizersPage moisturizersPage;
    private SunscreensPage sunscreensPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private PaymentPage paymentPage;

    // Test data
    private static final String APP_URL = "https://weathershopper.pythonanywhere.com/";
    private static final String BROWSER = "chrome"; // chrome, firefox, edge
    private static final String TEST_EMAIL = "test@weathershopper.com";
    private static final String TEST_CARD_NUMBER = "4242424242424242";
    private static final String TEST_EXPIRY_DATE = "1226";
    private static final String TEST_CVC = "123";
    private static final String TEST_ZIP = "75500";


    /**
     * Setup method - Runs before each test
     * Initializes WebDriver and page objects
     */
    @BeforeMethod
    public void setup() {
        System.out.println("========================================");
        System.out.println("Starting Weather Shopper Test");
        System.out.println("========================================");

        // Initialize WebDriver
        driver = DriverManager.initializeDriver(BROWSER);
        System.out.println("Browser launched: " + BROWSER);

        // Initialize Page Objects
        homePage = new HomePage(driver);
        moisturizersPage = new MoisturizersPage(driver);
        sunscreensPage = new SunscreensPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
        paymentPage = new PaymentPage(driver);
    }

    /**
     * Main test method - Complete weather shopper flow
     */
    @Test(priority = 1, description = "Complete Weather Shopper E2E Test")
    public void testWeatherShopperCompleteFlow() {
        System.out.println("\n=== Step 1: Navigate to Homepage ===");
        homePage.navigateToHomePage(APP_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("weathershopper"),
            "Failed to navigate to Weather Shopper homepage");
        System.out.println("Successfully navigated to homepage");

        System.out.println("\n=== Step 2: Verify Homepage Elements ===");
        Assert.assertTrue(homePage.isMoisturizersButtonDisplayed(),
            "Moisturizers button is not displayed");
        Assert.assertTrue(homePage.isSunscreensButtonDisplayed(),
            "Sunscreens button is not displayed");
        System.out.println("Homepage elements verified successfully");

        System.out.println("\n=== Step 3: Check Temperature and Navigate ===");
        int temperature = homePage.getTemperature();
        System.out.println("Current temperature: " + temperature + "°C");

        String productType = homePage.navigateBasedOnTemperature();
        System.out.println("Navigated to: " + productType + " page");

        System.out.println("\n=== Step 4: Select and Add Products to Cart ===");
        if (productType.equals("moisturizers")) {
            testMoisturizersFlow();
        } else if (productType.equals("sunscreens")) {
            testSunscreensFlow();
        }

        System.out.println("\n=== Step 5: Verify Cart and Checkout ===");
        testCartFlow();

        System.out.println("\n=== Step 6: Complete Payment ===");
        testPaymentFlow();

        System.out.println("\n========================================");
        System.out.println("Weather Shopper Test Completed Successfully!");
        System.out.println("========================================");
    }

    /**
     * Test moisturizers flow - Add Aloe and Almond moisturizers
     */
    private void testMoisturizersFlow() {
        System.out.println("\n--- Testing Moisturizers Flow ---");

        // Verify page is loaded
        Assert.assertTrue(moisturizersPage.isMoisturizersPageLoaded(),
            "Moisturizers page did not load properly");
        System.out.println("Moisturizers page loaded: " + moisturizersPage.getPageHeading());

        // Add least expensive Aloe moisturizer
        moisturizersPage.addLeastExpensiveAloe();

        // Add least expensive Almond moisturizer
        moisturizersPage.addLeastExpensiveAlmond();

        // Verify cart count
        Assert.assertTrue(moisturizersPage.verifyCartCount(2),
            "Cart count verification failed - Expected: 2");

        // Click cart button
        moisturizersPage.clickCartButton();
    }

    /**
     * Test sunscreens flow - Add SPF-50 and SPF-30 sunscreens
     */
    private void testSunscreensFlow() {
        System.out.println("\n--- Testing Sunscreens Flow ---");

        // Verify page is loaded
        Assert.assertTrue(sunscreensPage.isSunscreensPageLoaded(),
            "Sunscreens page did not load properly");
        System.out.println("Sunscreens page loaded: " + sunscreensPage.getPageHeading());

        // Add least expensive SPF-50 sunscreen
        sunscreensPage.addLeastExpensiveSPF50();

        // Add least expensive SPF-30 sunscreen
        sunscreensPage.addLeastExpensiveSPF30();

        // Verify cart count
        Assert.assertTrue(sunscreensPage.verifyCartCount(2),
            "Cart count verification failed - Expected: 2");

        // Click cart button
        sunscreensPage.clickCartButton();
    }

    /**
     * Test cart flow - Verify items and proceed to checkout
     */
    private void testCartFlow() {
        System.out.println("\n--- Testing Cart Flow ---");

        // Verify cart page is loaded
        Assert.assertTrue(cartPage.isCartPageLoaded(),
            "Cart page did not load properly");
        System.out.println("Cart page loaded: " + cartPage.getPageHeading());

        // Verify cart has 2 items
        Assert.assertTrue(cartPage.verifyCartItemCount(2),
            "Cart should contain 2 items");

        // Print all cart items
        cartPage.printAllCartItems();

        // Verify total price is displayed and valid
        Assert.assertTrue(cartPage.isTotalPriceDisplayed(),
            "Total price is not displayed");
        Assert.assertTrue(cartPage.isTotalPriceValid(),
            "Total price should be greater than 0");
        System.out.println("Total Price: " + cartPage.getTotalPrice());

        // Click Pay with Card (method will find button using multiple strategies)
        System.out.println("Attempting to click 'Pay with Card' button...");
        cartPage.clickPayWithCard();
    }

    /**
     * Test payment flow - Fill payment details and submit
     */
    private void testPaymentFlow() {
        System.out.println("\n--- Testing Payment Flow ---");

        // Complete payment with email and test card details
        System.out.println("Filling payment details with email: " + TEST_EMAIL);
        paymentPage.completePayment(TEST_EMAIL, TEST_CARD_NUMBER, TEST_EXPIRY_DATE, TEST_CVC ,TEST_ZIP);
        System.out.println("Payment details submitted");

        // Wait for payment to process
        System.out.println("Waiting for payment to process...");
        try {
            Thread.sleep(7000); // Wait 7 seconds for payment to complete and page to load
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify payment success message
        System.out.println("\n=== Verifying Payment Success Message ===");

        // Get the complete success message
        String successMessage = paymentPage.getSuccessMessage();
        System.out.println("\nComplete Success Message:");
        System.out.println("----------------------------------------");
        System.out.println(successMessage);
        System.out.println("----------------------------------------");

        // Assert 1: Verify "PAYMENT SUCCESS" heading is displayed
        Assert.assertTrue(paymentPage.verifyPaymentSuccessHeading(),
            "PAYMENT SUCCESS heading should be displayed");
        System.out.println("✓ Assertion 1 Passed: PAYMENT SUCCESS heading verified");

        // Assert 2: Verify main success message is displayed
        Assert.assertTrue(paymentPage.verifySuccessMessage(),
            "Payment success message should be displayed");
        System.out.println("✓ Assertion 2 Passed: Success message verified");

        // Assert 3: Verify complete message with follow-up text
        Assert.assertTrue(paymentPage.verifyFullSuccessMessage(),
            "Success message should contain: 'Your payment was successful. You should receive a follow-up call from our sales team.'");
        System.out.println("✓ Assertion 3 Passed: Complete message with follow-up text verified");

        // Assert 4: Verify exact text content
        Assert.assertTrue(successMessage.contains("PAYMENT SUCCESS"),
            "Message should contain heading: 'PAYMENT SUCCESS'");
        Assert.assertTrue(successMessage.contains("Your payment was successful"),
            "Message should contain: 'Your payment was successful'");
        Assert.assertTrue(successMessage.contains("follow-up call from our sales team"),
            "Message should contain: 'follow-up call from our sales team'");

        System.out.println("\n✅ ALL SUCCESS MESSAGE ASSERTIONS PASSED!");
        System.out.println("Payment flow completed successfully");
    }


    /**
     * Teardown method - Runs after each test
     * Closes browser and cleans up resources
     */
    @AfterMethod
    public void teardown() {
        System.out.println("\n=== Cleaning up ===");
        if (driver != null) {
            DriverManager.quitDriver();
            System.out.println("Browser closed successfully");
        }
    }
}
