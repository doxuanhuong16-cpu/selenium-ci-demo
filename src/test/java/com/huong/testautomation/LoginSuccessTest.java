package com.huong.testautomation;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginSuccessTest extends LoginTestBase {

    @Test
    public void loginSuccess() {
        driver = createDriver();

        String username = getRequiredEnv("TLU_USERNAME");
        String password = getRequiredEnv("TLU_PASSWORD");

        openLoginPageAndSubmit(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        Assert.assertFalse(
                driver.getCurrentUrl().contains("/login"),
                "Correct password should let the user leave the login page.");
    }
}
