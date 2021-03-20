package com.util;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.interfaces.webElementLocators.TENANTS;
import static com.interfaces.webElementLocators.TENANT_OUTLETS;

public class Helper {

    public static  void selectOutlet(String outletId, WebDriver webDriver)
    {
        By groupListItem = By.className(TENANTS);
        for (WebElement tenant : webDriver.findElements(By.className(TENANT_OUTLETS))) {
            for (WebElement outlet : tenant.findElements(groupListItem)) {
                if(outlet.getText().toLowerCase().trim().toLowerCase().contains(outletId.toLowerCase())) {
                    ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", outlet);
                    return;
                }
            }
        }
    }

    public static String getCookieID(WebDriver webDriver)
    {
        String[] cookies = webDriver.manage().getCookies().toString().split(" ");
        String cookie = null;
        for (String cookieItem : cookies ) {
            if(cookieItem.contains("SMSESSION")) {
                cookie = cookieItem;
                return cookie;
            }
        }
        return cookie;
    }

}
