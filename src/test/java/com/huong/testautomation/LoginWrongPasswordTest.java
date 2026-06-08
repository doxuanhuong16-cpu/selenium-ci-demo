package com.huong.testautomation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginWrongPasswordTest extends LoginTestBase {

    @Test
    public void loginFailWrongPassword() throws InterruptedException {
        driver = createDriver();

        String username = getRequiredEnv("TLU_USERNAME");
        String wrongPassword = "sai_mat_khau_123";

        openLoginPageAndSubmit(username, wrongPassword);

        Thread.sleep(3000);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "Wrong password should keep the user on the login page.");
    }
}
