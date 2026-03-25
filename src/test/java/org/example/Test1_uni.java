package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class Test1_uni {

    private WebDriver chromeDriver;
    private static final String baseUrl = "https://nmu.org.ua/";

    @BeforeClass
    public void setUpBrowser() {
        WebDriverManager.firefoxdriver().setup();
        chromeDriver = new FirefoxDriver();
        chromeDriver.manage().window().maximize();
        chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @BeforeMethod
    public void openHomePage() {
        chromeDriver.get(baseUrl);
    }

    @AfterClass
    public void tearDown() {
        if (chromeDriver != null) {
            chromeDriver.quit();
        }
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void testFindHeaderByWrongId() {
        chromeDriver.findElement(By.id("heder")); //На сайті було виправлено з heder на правильний варіант - header
    }

    @Test
    public void testPageTitle() {
        String actualTitle = chromeDriver.getTitle();
        Assert.assertTrue(actualTitle.contains("Дніпровська політехніка"),
                "Назва сторінки не містить очікуваний текст.");
    }

    @Test
    public void testClickVstup2026ByXPath() {
        WebElement vstupLink = chromeDriver.findElement(
                By.xpath("(//a[contains(., 'ВСТУП 2026')])[1]")
        );

        String href = vstupLink.getAttribute("href");
        Assert.assertNotNull(href, "Посилання не має href");

        chromeDriver.get(href);

        String currentUrl = chromeDriver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("nmu.org.ua") || currentUrl.contains("old.nmu.org.ua"),
                "Перехід за посиланням не відбувся коректно.");
    }

    @Test
    public void testSearchFieldOnStudentPage() {

        String studentPageUrl = "content/student_life/students/";
        chromeDriver.get(baseUrl + studentPageUrl);

        WebElement searchField = chromeDriver.findElement(By.tagName("input"));

        Assert.assertNotNull(searchField);

        System.out.println("Name: " + searchField.getAttribute("name"));
        System.out.println("Id: " + searchField.getAttribute("id"));
        System.out.println("Type: " + searchField.getAttribute("type"));
        System.out.println("Value: " + searchField.getAttribute("value"));

        String inputValue = "test";
        searchField.sendKeys(inputValue);

        Assert.assertEquals(searchField.getAttribute("value"), inputValue);

        searchField.sendKeys(Keys.ENTER);

        Assert.assertNotEquals(chromeDriver.getCurrentUrl(), baseUrl + studentPageUrl);
    }
}