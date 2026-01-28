# Weather Shopper - Selenium Java Automation

This project contains automated tests for the Weather Shopper application using Selenium WebDriver, Java, and TestNG.

## Application Under Test
- **URL**: https://weathershopper.pythonanywhere.com/
- **Description**: Weather-based shopping application for moisturizers and sunscreens

## Test Scenarios Covered

### Complete E2E Flow
1. Navigate to Weather Shopper homepage
2. Read the current temperature
3. Based on temperature:
   - **If temperature < 19°C**: Navigate to Moisturizers
     - Add least expensive moisturizer containing **Aloe**
     - Add least expensive moisturizer containing **Almond**
   - **If temperature > 34°C**: Navigate to Sunscreens
     - Add least expensive sunscreen with **SPF-50**
     - Add least expensive sunscreen with **SPF-30**
4. Verify cart contains 2 items
5. Proceed to checkout
6. Complete payment with test card:
   - Card Number: 4242 4242 4242 4242
   - Expiry Date: Any valid future date (e.g., 12/25)
   - CVC: Any 3-digit number (e.g., 123)

## Project Structure

```
WeatherShopper-Java-Selenium/
├── pom.xml                          # Maven configuration
├── README.md                        # Project documentation
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── pages/              # Page Object Model classes
│   │       │   ├── BasePage.java
│   │       │   ├── HomePage.java
│   │       │   ├── MoisturizersPage.java
│   │       │   ├── SunscreensPage.java
│   │       │   ├── CartPage.java
│   │       │   ├── CheckoutPage.java
│   │       │   └── PaymentPage.java
│   │       └── utils/              # Utility classes
│   │           └── DriverManager.java
│   └── test/
│       ├── java/
│       │   └── tests/             # Test classes
│       │       └── WeatherShopperTest.java
│       └── resources/
│           └── testng.xml         # TestNG configuration
```

## Technologies & Tools

- **Java**: 11+
- **Selenium WebDriver**: 4.16.1
- **TestNG**: 7.8.0
- **WebDriverManager**: 5.6.3 (Automatic driver management)
- **Maven**: Build and dependency management
- **ExtentReports**: 5.1.1 (Test reporting)

## Prerequisites

1. **Java JDK 11 or higher**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Verify installation: `java -version`

2. **Maven**
   - Download: https://maven.apache.org/download.cgi
   - Verify installation: `mvn -version`

3. **Supported Browsers**
   - Chrome (recommended)
   - Firefox
   - Edge

## Setup Instructions

### 1. Clone or Download the Project
```bash
cd WeatherShopper-Java-Selenium
```

### 2. Install Dependencies
```bash
mvn clean install
```

This will download all required dependencies including Selenium, TestNG, and WebDriverManager.

### 3. Update Test Configuration (Optional)
Edit `src/test/java/tests/WeatherShopperTest.java` to change:
- Browser type (default: Chrome)
- Test card details
- Application URL

```java
private static final String BROWSER = "chrome"; // chrome, firefox, edge
private static final String APP_URL = "https://weathershopper.pythonanywhere.com/";
```

## Running Tests

### Method 1: Using Maven Command
```bash
mvn clean test
```

### Method 2: Using TestNG XML
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

### Method 3: From IDE (IntelliJ IDEA / Eclipse)
1. Right-click on `WeatherShopperTest.java`
2. Select "Run 'WeatherShopperTest'"

Or right-click on `testng.xml` and select "Run"

### Method 4: Run Specific Test
```bash
mvn test -Dtest=WeatherShopperTest#testWeatherShopperCompleteFlow
```

## Test Execution Flow

```
1. Launch Browser (Chrome by default)
   ↓
2. Navigate to Homepage
   ↓
3. Read Temperature
   ↓
4. Decision Based on Temperature
   ├─→ < 19°C: Buy Moisturizers (Aloe + Almond)
   └─→ > 34°C: Buy Sunscreens (SPF-50 + SPF-30)
   ↓
5. Add 2 Products to Cart (Least Expensive)
   ↓
6. Verify Cart (2 items, valid total)
   ↓
7. Proceed to Checkout
   ↓
8. Fill Payment Details (Test Card)
   ↓
9. Submit Payment
   ↓
10. Verify Success
    ↓
11. Close Browser
```

## Key Features

### Page Object Model (POM)
- Clean separation of page elements and test logic
- Reusable page classes
- Easy maintenance and scalability

### Explicit Waits
- All interactions use explicit waits
- No hard-coded `Thread.sleep()` (except for payment processing)
- Robust element handling

### Automatic Driver Management
- WebDriverManager handles browser driver binaries
- No manual driver downloads required
- Supports Chrome, Firefox, Edge

### Comprehensive Assertions
- Temperature validation
- Cart item verification
- Price validation
- Element visibility checks

### Detailed Logging
- Console output for each step
- Easy debugging
- Test flow visibility

## Test Cases

### 1. testWeatherShopperCompleteFlow (Main Test)
- **Priority**: 1
- **Status**: Enabled
- **Description**: Complete end-to-end flow from homepage to payment

### 2. testMoisturizersOnly
- **Priority**: 2
- **Status**: Disabled (can be enabled for testing)
- **Description**: Tests only the Moisturizers flow

### 3. testSunscreensOnly
- **Priority**: 3
- **Status**: Disabled (can be enabled for testing)
- **Description**: Tests only the Sunscreens flow

### 4. testTemperatureDisplay
- **Priority**: 4
- **Status**: Enabled
- **Description**: Verifies temperature display and valid range

## Customization

### Change Browser
In `WeatherShopperTest.java`:
```java
private static final String BROWSER = "firefox"; // or "edge"
```

### Enable Headless Mode
In `DriverManager.java`, uncomment:
```java
chromeOptions.addArguments("--headless");
```

### Run Individual Flows
Enable specific tests in `WeatherShopperTest.java`:
```java
@Test(priority = 2, enabled = true, description = "Test Moisturizers Flow Only")
public void testMoisturizersOnly() { ... }
```

## Troubleshooting

### Issue: Browser driver not found
**Solution**: WebDriverManager should handle this automatically. If issues persist:
```bash
mvn clean install -U
```

### Issue: Element not found
**Solution**: Check if the application is loaded properly. Increase wait timeout in `BasePage.java`:
```java
private static final int DEFAULT_TIMEOUT = 20; // Increase from 15
```

### Issue: Payment not completing
**Solution**: The Stripe iframe may have changed. Update locators in `PaymentPage.java`

### Issue: Tests fail due to temperature
**Solution**: The application temperature changes. Ensure it's either < 19°C or > 34°C

## Best Practices Implemented

1. **Page Object Model**: Separation of test logic and page elements
2. **Explicit Waits**: No hard waits, all elements wait for visibility
3. **Reusable Components**: BasePage with common methods
4. **Clean Code**: Meaningful method names and comments
5. **Assertions**: Comprehensive validation at each step
6. **Error Handling**: Try-catch blocks for optional elements
7. **Logging**: Console output for debugging
8. **Configuration**: Easy to modify test parameters

## Test Data

### Moisturizers Test Data
- **Ingredient 1**: Aloe
- **Ingredient 2**: Almond
- **Selection Criteria**: Least expensive for each ingredient

### Sunscreens Test Data
- **SPF Level 1**: SPF-50
- **SPF Level 2**: SPF-30
- **Selection Criteria**: Least expensive for each SPF level

### Payment Test Data
- **Card Number**: 4242 4242 4242 4242 (Stripe test card)
- **Expiry Date**: 12/25 (MM/YY format)
- **CVC**: 123

## Reporting

After test execution, view results in:
- **Console Output**: Detailed step-by-step execution
- **TestNG Reports**: `target/surefire-reports/index.html`
- **Maven Reports**: `target/surefire-reports/`

## Future Enhancements

- [ ] Add ExtentReports HTML reporting
- [ ] Add screenshot capture on failure
- [ ] Implement data-driven testing with Excel/CSV
- [ ] Add parallel test execution
- [ ] Integrate with CI/CD (Jenkins, GitHub Actions)
- [ ] Add API testing for backend validation
- [ ] Implement cross-browser testing grid

## Contributing

To add new test cases:
1. Create page objects in `src/main/java/pages/`
2. Add test methods in `src/test/java/tests/`
3. Update `testng.xml` to include new tests
4. Follow existing naming conventions

## Support

For issues or questions:
1. Check the Troubleshooting section
2. Review console output for error details
3. Verify element locators are current
4. Ensure application is accessible

## License

This project is created for educational and testing purposes.

## Author

Created for Weather Shopper automation testing demonstration.

---

**Note**: This project uses Stripe test card numbers. Do not use real payment information.
