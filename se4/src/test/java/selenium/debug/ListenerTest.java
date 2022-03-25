package selenium.debug;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

public class ListenerTest {

    @Test(invocationCount = 10)
    public void se4Test() {
        var webDriver = WebDriverManager.chromiumdriver().create();

        var eventFiringDecorator = new EventFiringDecorator(new WebDriverListenerImpl());
        var eventFiringWebDriver = eventFiringDecorator.decorate(webDriver);
        var wait = new WebDriverWait(eventFiringWebDriver, Duration.ofSeconds(10));
        eventFiringWebDriver.get("https://the-internet.herokuapp.com/iframe");
        var until = wait.until(driver -> driver.findElement(By.cssSelector("#tinymce")));

        var wrappedDriver = ((WrapsDriver) until).getWrappedDriver();
        var newDecorate = new EventFiringDecorator(new WebDriverListenerImpl()).decorate(wrappedDriver);
        var newWait = new WebDriverWait(newDecorate, Duration.ofSeconds(10));
        newWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".someNonExistingElement")));
        wrappedDriver.close();
        wrappedDriver.quit();
    }

}