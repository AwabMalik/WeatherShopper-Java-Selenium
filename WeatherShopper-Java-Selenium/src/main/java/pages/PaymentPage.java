package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * PaymentPage - Represents the Stripe payment form
 * Handles payment card details entry and form submission
 */
public class PaymentPage extends BasePage {

    // Stripe iframe locator
    private By stripeIframe = By.xpath("//iframe[contains(@name,'stripe_checkout')]");

    // Locators inside Stripe iframe
    private By emailField = By.id("email");
    private By emailFieldAlt = By.xpath("//input[@type='email']");
    private By emailFieldAlt2 = By.xpath("//input[contains(@placeholder,'Email') or contains(@placeholder,'email')]");
    private By cardNumberField = By.xpath("//input[@placeholder='Card number']");
    private By expiryDateField = By.xpath("//input[@placeholder='MM / YY']");
    private By cvcField = By.xpath("//input[@placeholder='CVC']");
    private By zipCodeField = By.xpath("//input[@placeholder='ZIP Code']");
    private By payButton = By.xpath("//button[@type='submit']");
    private By submitButton = By.id("submitButton");

    // Success message locators (after payment)
    // Message format: "PAYMENT SUCCESS" (heading) + "Your payment was successful. You should receive a follow-up call from our sales team." (body)
    private By successMessageHeading = By.xpath("//*[contains(text(),'PAYMENT SUCCESS')]");
    private By successMessageBody = By.xpath("//*[contains(text(),'Your payment was successful. You should receive a follow-up call from our sales team.')]");
    private By successMessageBodyAlt = By.xpath("//*[contains(text(),'Your payment was successful')]");
    private By followUpText = By.xpath("//*[contains(text(),'follow-up call from our sales team')]");

    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public PaymentPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Switch to Stripe payment iframe
     */
    public void switchToPaymentFrame() {
        try {
            // Wait for iframe to be available and switch to it
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(stripeIframe));
            System.out.println("Switched to Stripe payment iframe");
        } catch (Exception e) {
            System.out.println("Could not switch to iframe, attempting direct interaction: " + e.getMessage());
        }
    }

    /**
     * Fill email field (REQUIRED - tries multiple locator strategies)
     * @param email Email address
     */
    public void enterEmail(String email) {
        System.out.println("Attempting to enter email: " + email);
        WebElement emailElement = null;

        // Try multiple locator strategies for email field
        try {
            emailElement = waitForElementVisible(emailField);
            System.out.println("Found email field using primary locator (id=email)");
        } catch (Exception e1) {
            System.out.println("Primary email locator failed, trying alternative locators...");
            try {
                emailElement = waitForElementVisible(emailFieldAlt);
                System.out.println("Found email field using type=email");
            } catch (Exception e2) {
                try {
                    emailElement = waitForElementVisible(emailFieldAlt2);
                    System.out.println("Found email field using placeholder strategy");
                } catch (Exception e3) {
                    System.out.println("WARNING: Email field not found, but will continue...");
                    return;
                }
            }
        }

        if (emailElement != null) {
            emailElement.clear();
            emailElement.sendKeys(email);
            System.out.println("✓ Successfully entered email: " + email);
        }
    }

    /**
     * Fill card number field (Stripe special handling)
     * @param cardNumber Card number (e.g., "4242424242424242" - NO SPACES)
     */
    public void enterCardNumber(String cardNumber) {
        System.out.println("Attempting to enter card number: " + maskCardNumber(cardNumber));

        // Remove any spaces from card number
        String cleanCardNumber = cardNumber.replaceAll("\\s+", "");

        try {
            WebElement cardField = waitForElementVisible(cardNumberField);

            // Click to focus the field
            cardField.click();

            // Clear any existing content
            cardField.clear();

            // Wait after clearing
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {}

            // Send keys character by character for Stripe's special handling
            System.out.println("Entering card number character by character...");
            for (int i = 0; i < cleanCardNumber.length(); i++) {
                char digit = cleanCardNumber.charAt(i);
                cardField.sendKeys(String.valueOf(digit));

                // Small pause between characters
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {}
            }

            // Wait to let Stripe process the input
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}

            System.out.println("✓ Successfully entered card number: " + maskCardNumber(cleanCardNumber));

            // Verify the field has content
            String fieldValue = cardField.getAttribute("value");
            if (fieldValue == null || fieldValue.isEmpty()) {
                System.out.println("⚠ WARNING: Card number field appears empty after entry");
            } else {
                System.out.println("✓ Card field verification: Field has " + fieldValue.length() + " characters (formatted: " + fieldValue + ")");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR entering card number: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Mask card number for logging (show last 4 digits only)
     * @param cardNumber Full card number
     * @return Masked card number (e.g., "************4242")
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleanNumber = cardNumber.replaceAll("\\s+", "");
        int length = cleanNumber.length();
        return "*".repeat(length - 4) + cleanNumber.substring(length - 4);
    }

    /**
     * Fill expiry date field (Stripe special handling)
     * @param expiryDate Expiry date in MMYY format (e.g., "1226" for December 2026)
     *                   Note: Stripe accepts MMYY format, it will auto-format to MM / YY
     */
    public void enterExpiryDate(String expiryDate) {
        System.out.println("Attempting to enter expiry date: " + expiryDate);

        try {
            WebElement expiryField = waitForElementVisible(expiryDateField);

            // Click to focus the field
            expiryField.click();

            // Clear any existing content
            expiryField.clear();

            // Wait after clearing
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}

            // Remove any slashes or spaces from input
            String cleanExpiry = expiryDate.replaceAll("[^0-9]", "");

            // Send keys character by character
            System.out.println("Entering expiry date: " + cleanExpiry);
            for (int i = 0; i < cleanExpiry.length(); i++) {
                char digit = cleanExpiry.charAt(i);
                expiryField.sendKeys(String.valueOf(digit));

                // Small pause between characters
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }

            // Wait to let Stripe process
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}

            // Verify field value
            String fieldValue = expiryField.getAttribute("value");
            System.out.println("✓ Successfully entered expiry date: " + cleanExpiry + " (displayed as: " + fieldValue + ")");

        } catch (Exception e) {
            System.out.println("❌ ERROR entering expiry date: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fill CVC field (Stripe special handling)
     * @param cvc CVC code (e.g., "123")
     */
    public void enterCVC(String cvc) {
        System.out.println("Attempting to enter CVC...");

        try {
            WebElement cvcFieldElement = waitForElementVisible(cvcField);

            // Click to focus the field
            cvcFieldElement.click();

            // Clear any existing content
            cvcFieldElement.clear();

            // Wait after clearing
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}

            // Send keys character by character
            for (int i = 0; i < cvc.length(); i++) {
                char digit = cvc.charAt(i);
                cvcFieldElement.sendKeys(String.valueOf(digit));

                // Small pause between characters
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }

            // Wait to let Stripe process
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}

            System.out.println("✓ Successfully entered CVC: *** (" + cvc.length() + " digits)");

        } catch (Exception e) {
            System.out.println("❌ ERROR entering CVC: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fill ZIP code field (Stripe special handling)
     * @param zipCode ZIP code (e.g., "75500") - can be null to skip
     */
    public void enterZipCode(String zipCode) {
        // Skip if zipCode is null or empty
        if (zipCode == null || zipCode.trim().isEmpty()) {
            System.out.println("⚠ ZIP code not provided - skipping");
            return;
        }

        System.out.println("Attempting to enter ZIP code: " + zipCode);

        try {
            WebElement zipField = waitForElementVisible(zipCodeField);

            // Click to focus
            zipField.click();

            // Clear with wait
            zipField.clear();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}

            // Character-by-character entry
            for (int i = 0; i < zipCode.length(); i++) {
                char digit = zipCode.charAt(i);
                zipField.sendKeys(String.valueOf(digit));

                // Small pause between characters
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }

            // Wait to let Stripe process
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}

            System.out.println("✓ Successfully entered ZIP code: " + zipCode);

        } catch (Exception e) {
            System.out.println("⚠ ZIP code field not required or not found (this is OK for some regions)");
        }
    }

    /**
     * Fill payment details with email and ZIP (RECOMMENDED)
     * @param email Email address
     * @param cardNumber Card number (without spaces)
     * @param expiryDate Expiry date in MMYY format (e.g., "1226")
     * @param cvc CVC code
     * @param zipCode ZIP code (e.g., "75500")
     */
    public void fillPaymentDetails(String email, String cardNumber, String expiryDate, String cvc, String zipCode) {
        System.out.println("\n--- Filling Payment Details ---");

        enterEmail(email);
        waitBetweenFields();

        enterCardNumber(cardNumber);
        waitBetweenFields();

        enterExpiryDate(expiryDate);
        waitBetweenFields();

        enterCVC(cvc);
        waitBetweenFields();

        enterZipCode(zipCode);

        System.out.println("--- All Payment Fields Completed ---\n");
    }

    /**
     * Fill payment details without ZIP (for regions that don't require it)
     * @param email Email address
     * @param cardNumber Card number (without spaces)
     * @param expiryDate Expiry date in MMYY format (e.g., "1226")
     * @param cvc CVC code
     */
    public void fillPaymentDetails(String email, String cardNumber, String expiryDate, String cvc) {
        fillPaymentDetails(email, cardNumber, expiryDate, cvc, null);
    }

    /**
     * Small wait between field entries for stability
     * Increased to 1000ms due to character-by-character entry
     */
    private void waitBetweenFields() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Click Pay/Submit button to complete payment
     */
    public void clickPayButton() {
        try {
            // Try to find and click pay button
            if (isDisplayed(payButton)) {
                click(payButton);
                System.out.println("Clicked Pay button");
            } else if (isDisplayed(submitButton)) {
                click(submitButton);
                System.out.println("Clicked Submit button");
            }
        } catch (Exception e) {
            System.out.println("Error clicking pay button: " + e.getMessage());
        }
    }

    /**
     * Switch back to default content (exit iframe)
     */
    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        System.out.println("Switched back to default content");
    }

    /**
     * Complete payment flow with email and card details (RECOMMENDED)
     *
     * @param email      Email address
     * @param cardNumber Card number
     * @param expiryDate Expiry date in MMYY format (e.g., "1226")
     * @param cvc        CVC code
     * @param zipCode    ZIP code
     */
    public void completePayment(String email, String cardNumber, String expiryDate, String cvc, String zipCode) {
        switchToPaymentFrame();
        fillPaymentDetails(email, cardNumber, expiryDate, cvc, zipCode);
        clickPayButton();
        switchToDefaultContent();
    }

    /**
     * Verify payment success message is displayed
     * @return true if success message is visible
     */
    public boolean isPaymentSuccessful() {
        try {
            // Wait a bit for payment to process
            Thread.sleep(3000);
            return isDisplayed(successMessageHeading);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get complete success message text (heading + body)
     * Format: "PAYMENT SUCCESS\nYour payment was successful. You should receive a follow-up call from our sales team."
     * @return Success message text
     */
    public String getSuccessMessage() {
        try {
            // Wait for page to load after payment
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        StringBuilder fullMessage = new StringBuilder();

        // Get heading: "PAYMENT SUCCESS"
        try {
            if (isDisplayed(successMessageHeading)) {
                String heading = getText(successMessageHeading);
                fullMessage.append(heading);
                System.out.println("Found heading: " + heading);
            }
        } catch (Exception e) {
            System.out.println("Heading not found");
        }

        // Get body message
        try {
            if (isDisplayed(successMessageBody)) {
                String body = getText(successMessageBody);
                if (fullMessage.length() > 0) {
                    fullMessage.append("\n");
                }
                fullMessage.append(body);
                System.out.println("Found body: " + body);
            }
        } catch (Exception e) {
            // Try alternative locator
            try {
                if (isDisplayed(successMessageBodyAlt)) {
                    String body = getText(successMessageBodyAlt);
                    if (fullMessage.length() > 0) {
                        fullMessage.append("\n");
                    }
                    fullMessage.append(body);
                    System.out.println("Found body (alt): " + body);
                }
            } catch (Exception e2) {
                System.out.println("Body message not found");
            }
        }

        if (fullMessage.length() == 0) {
            return "Success message not found";
        }

        return fullMessage.toString();
    }

    /**
     * Verify "PAYMENT SUCCESS" heading is displayed
     * @return true if heading is found
     */
    public boolean verifyPaymentSuccessHeading() {
        try {
            Thread.sleep(2000);
            return isDisplayed(successMessageHeading);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify the complete success message is displayed
     * @return true if the expected success message is found
     */
    public boolean verifySuccessMessage() {
        String message = getSuccessMessage();
        System.out.println("✓ Success message received: " + message);
        return message.contains("Your payment was successful") ||
               message.contains("PAYMENT SUCCESS");
    }

    /**
     * Verify the complete success message with follow-up text
     * Expected: "Your payment was successful. You should receive a follow-up call from our sales team."
     * @return true if the full expected message is found
     */
    public boolean verifyFullSuccessMessage() {
        String message = getSuccessMessage();
        boolean hasMainMessage = message.contains("Your payment was successful");
        boolean hasFollowUpText = message.contains("follow-up call from our sales team");

        System.out.println("✓ Payment Success Heading: " + message.contains("PAYMENT SUCCESS"));
        System.out.println("✓ Main Message: " + hasMainMessage);
        System.out.println("✓ Follow-up Text: " + hasFollowUpText);

        return hasMainMessage && hasFollowUpText;
    }

    /**
     * Wait for payment to complete
     * @param timeoutInSeconds Timeout in seconds
     * @return true if payment completed successfully
     */
    public boolean waitForPaymentSuccess(int timeoutInSeconds) {
        try {
            Thread.sleep(timeoutInSeconds * 1000);
            return isPaymentSuccessful();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
