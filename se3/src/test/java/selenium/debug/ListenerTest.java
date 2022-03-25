package selenium.debug;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

public class ListenerTest {

    @Test(invocationCount = 10)
    public void uploadTest() {
        var webDriver = WebDriverManager.chromiumdriver().create();

        var eventFiringWebDriver = new EventFiringWebDriver(webDriver);
        eventFiringWebDriver.register(new WebDriverListenerImpl());
        var wait = new WebDriverWait(eventFiringWebDriver, 10);
        eventFiringWebDriver.get("https://the-internet.herokuapp.com/iframe");
        var until = wait.until(driver -> driver.findElement(By.cssSelector("#tinymce")));

        var wrappedDriver = ((WrapsDriver) until).getWrappedDriver();
        var newEventFiringWebDriver = new EventFiringWebDriver(wrappedDriver);
        newEventFiringWebDriver.register(new WebDriverListenerImpl());
        var newWait = new WebDriverWait(newEventFiringWebDriver, 10);
        newWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".someNonExistingElement")));
        wrappedDriver.close();
        wrappedDriver.quit();
    }
}