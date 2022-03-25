package selenium.debug;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static java.util.Objects.isNull;

@Slf4j
public class WebDriverListenerImpl implements WebDriverListener {
    private static final By IFRAME = By.tagName("iframe");

    @Override
    public void beforeFindElement(WebElement element, By locator) {
        handleIframes(element, locator);
    }

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        handleIframes(locator, null, driver);
    }

    @Override
    public void beforeFindElements(WebDriver driver, By locator) {
        handleIframes(locator, null, driver);
    }

    @Override
    public void beforeFindElements(WebElement element, By locator) {
        handleIframes(element, locator);
    }

    private void handleIframes(WebElement element, By locator) {
        WebDriver driver = ((WrapsDriver) element).getWrappedDriver();
        handleIframes(locator, element, driver);
    }

    private void handleIframes(By by, WebElement element, WebDriver driver) {
        if (isNull(element) && !isElementFound(by, driver)) { //search within the context of the element or element is found in the current iFrame
            driver.switchTo().defaultContent();
            lookUpForElement(by, driver);
        }
    }

    private boolean lookUpForElement(By by, WebDriver driver) {
        if (isElementFound(by, driver)) {
            return true;
        }
        //no element found in the current context
        try {
            var webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            var iframes = driver.findElements(IFRAME);
            for (int i = 0; i < iframes.size(); i++) {
                webDriverWait.until(frameToBeAvailableAndSwitchToIt(i)); //so search inside the iframe
                if (lookUpForElement(by, driver)) return true;
            }
        } catch (NoSuchFrameException noSuchFrameException) {
            //do nothing, most probably there is no iFrame
        }
        return false;
    }

    private boolean isElementFound(By by, WebDriver driver) {
        return !driver.findElements(by).isEmpty();
    }

    private static ExpectedCondition<WebDriver> frameToBeAvailableAndSwitchToIt(final int iFrameIndex) {
        return new ExpectedCondition<>() {
            @Override
            public WebDriver apply(WebDriver driver) {
                try {
                    return driver.switchTo().frame(iFrameIndex);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "frame with index to be available: " + iFrameIndex;
            }
        };
    }
}