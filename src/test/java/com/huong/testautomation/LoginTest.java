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
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class LoginTest {

    private WebDriver driver;

    private WebDriver createDriver() {
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

    @Test
    public void loginSuccess() {
        driver = createDriver();

        String username = getRequiredEnv("TLU_USERNAME");
        String password = getRequiredEnv("TLU_PASSWORD");

        driver.get("https://sinhvien1.tlu.edu.vn/#/login");

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

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        Assert.assertFalse(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    public void loginFailWrongPassword() throws InterruptedException {
        driver = createDriver();

        String username = getRequiredEnv("TLU_USERNAME");
        String wrongPassword = "sai_mat_khau_123";

        driver.get("https://sinhvien1.tlu.edu.vn/#/login");

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
        typeValue(passwordInput, wrongPassword);

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button")));
        loginButton.click();

        Thread.sleep(3000);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "Wrong password should keep the user on the login page.");
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

    private String getRequiredEnv(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new SkipException("Missing environment variable: " + name);
        }

        return value;
    }
}
