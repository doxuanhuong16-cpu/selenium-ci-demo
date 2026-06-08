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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class LoginTest {

    private static final String LOGIN_URL = "https://sinhvien1.tlu.edu.vn/#/login";
    private static final String USERNAME = "2351067096";
    private static final String CORRECT_PASSWORD = "xuanhuong2005";
    private static final String WRONG_PASSWORD = "sai_mat_khau_123";

    private WebDriver driver;

    @Test
    public void loginSuccess() {
        driver = createDriver();

        openLoginPageAndSubmit(USERNAME, CORRECT_PASSWORD);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        Assert.assertFalse(
                driver.getCurrentUrl().contains("/login"),
                "Correct password should let the user leave the login page.");
    }

    @Test
    public void loginWrongPasswordShouldStayOnLoginPage() throws InterruptedException {
        driver = createDriver();

        openLoginPageAndSubmit(USERNAME, WRONG_PASSWORD);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        Assert.assertFalse(
                driver.getCurrentUrl().contains("/login"),
                "This test intentionally fails when the wrong password cannot log in.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

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

    private void openLoginPageAndSubmit(String username, String password) {
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
