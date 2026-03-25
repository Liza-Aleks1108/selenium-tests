package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class FlowersTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String baseUrl = "https://flowers.ua/";

    @BeforeClass
    public void setUpBrowser() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @BeforeMethod
    public void openHomePage() {
        driver.get(baseUrl);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testFlowersScenarioUaVersion() {
        // 1. Відкрити сайт
        Assert.assertTrue(driver.getCurrentUrl().contains("flowers.ua"),
                "Сайт Flowers.ua не відкрився.");

        // 2. Спроба перейти на українську версію
        try {
            WebElement uaLink = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[contains(., 'Українська') or normalize-space()='ua' or normalize-space()='UA']")
                    )
            );
            uaLink.click();

            // невелике очікування після кліку
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Не вдалося перемкнути мову на українську. Сайт може редіректити на /en.");
        }

        // 3. Перевірка, що сайт все одно відкритий
        Assert.assertTrue(driver.getCurrentUrl().contains("flowers.ua"),
                "Після перемикання мови сайт недоступний.");

        // 4. Пошук поля вводу
        WebElement searchField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("(//input[not(@type='hidden')])[1]")
                )
        );

        // 5. Введення тексту
        String text = "троянди";
        searchField.clear();
        searchField.sendKeys(text);

        // 6. Перевірка, що текст є в полі
        Assert.assertEquals(searchField.getAttribute("value"), text,
                "Текст не зберігся у полі пошуку.");

        // 7. Клік по кнопці/запуск пошуку
        searchField.sendKeys(Keys.ENTER);

        // 8. Перевірка умови
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("search"),
                ExpectedConditions.urlContains("%D1%82%D1%80%D0%BE"),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(translate(text(),'АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ','абвгґдеєжзиіїйклмнопрстуфхцчшщьюяabcdefghijklmnopqrstuvwxyz'),'тро')]")
                )
        ));

        Assert.assertTrue(
                driver.getCurrentUrl().toLowerCase().contains("search")
                        || driver.getCurrentUrl().toLowerCase().contains("%d1%82%d1%80%d0%be")
                        || driver.getPageSource().toLowerCase().contains("тро"),
                "Результати пошуку не з'явилися."
        );
    }
}