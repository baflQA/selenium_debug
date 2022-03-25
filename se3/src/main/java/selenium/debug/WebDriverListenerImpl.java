package selenium.debug;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
public class WebDriverListenerImpl extends AbstractWebDriverEventListener {
    private static final By IFRAME = By.tagName("iframe");

    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver driver) {
        log.info("Looking for: '{}' element", by);
        handleIframes(by, webElement, driver);
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
            WebDriverWait webDriverWait = new WebDriverWait(driver, 5);
            List<WebElement> iframes = driver.findElements(IFRAME);
            boolean isFound;
            for (int i = 0; i < iframes.size(); i++) {
                webDriverWait.until(frameToBeAvailableAndSwitchToIt(i)); //so search inside the iframe
                isFound = lookUpForElement(by, driver);
                if (isFound) return true;
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