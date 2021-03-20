package com.ui;

import com.util.TestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class SGATELandingPage {

    @FindBy(how = How.NAME, using = "USER")
    static WebElement username;
    @FindBy(how = How.NAME, using = "PASSWORD")
    static WebElement password;
    @FindBy(how = How.NAME, using = "LanguageSelection")
    static WebElement languageSelection;
    @FindBy(how = How.ID, using = "loginbutton")
    static WebElement loginbutton;

    protected static WebDriver webDriver;

    public static void loginToTestDriveApplication()
    {
        PageFactory.initElements(webDriver, SGATELandingPage.class);
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.MINUTES);
        webDriver.manage().window().maximize();
        webDriver.get(TestData.getDataItem("tda.url"));
        username.sendKeys(TestData.getDataItem("tda.username"));
        password.sendKeys(TestData.getPassword());
        languageSelection.click();
        Select dropdown = new Select(languageSelection);
        dropdown.selectByVisibleText(TestData.getDataItem("tda.preferredLanguage"));
        loginbutton.click();
    }

    public static WebDriver getWebDriver()
    {
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        System.setProperty("webdriver.chrome.driver", "src/drivers/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("test-type");
        options.addArguments("start-maximized");
        options.addArguments("--enable-automation");
        options.addArguments("test-type=browser");
        options.addArguments("disable-infobars");
        options.setCapability("e34:token", "ea703494-acb0-43");
        options.setCapability("e34:video", true);
        options.setCapability("e34:per_test_timeout_ms", "600000");

        //webDriver = new ChromeDriver(options);

        try {
            webDriver = new RemoteWebDriver(new URL("http://seleniumbox.bmwgroup.net/wd/hub"), options);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        loginToTestDriveApplication();
        return webDriver;
    }
}
