package com.huong.testautomation;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;

public abstract class LoginTestBase {

    protected static final String LOGIN_URL = "https://sinhvien1.tlu.edu.vn/#/login";

    protected WebDriver driver;

    protected WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();

        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        if ("true".equalsIgnoreCase(System.getenv("CI"))) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver chromeDriver = new ChromeDriver(options);
        chromeDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        chromeDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));
        return chromeDriver;
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    protected void openLoginPageAndSubmit(String username, String password) {
        driver.get(LOGIN_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        List<WebElement> inputs = wait.until(d -> {
            List<WebElement> visibleInputs = d.findElements(By.cssSelector("input")).stream()
                    .filter(WebElement::isDisplayed)
                    .toList();
            return visibleInputs.size() >= 2 ? visibleInputs : null;
        });

        WebElement usernameInput = inputs.get(0);
        WebElement passwordInput = inputs.get(1);

        typeValue(usernameInput, username);
        typeValue(passwordInput, password);

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button")));
        loginButton.click();
    }

    protected String getRequiredEnv(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new SkipException("Missing environment variable: " + name);
        }

        return value;
    }

    private void typeValue(WebElement element, String value) {
        element.click();
        element.clear();
        element.sendKeys(value);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                        + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                element);
    }
}
