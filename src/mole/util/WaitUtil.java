/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mole.util;

import com.google.common.base.Function;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * Two usage examples in your test code:
 * WaitUtil myWaiter = new WaitUtil(driver);
   WebElement search = myWaiter.waitForMe(By.name("btnG"), 10);
   or
   if(!myWaiter.waitForMe(By.name("btnG"), 1, 10)) return;
   or if (!myWaiter.waitForMeDisappear(By.name("btnG"), 10)) return;
 * @author cchi
 */
public class WaitUtil {

        public static final Logger ppcLogger = (Logger) LoggerFactory.getLogger("PPCLogger");

	public WebElement waitForMe(WebDriver driver, By locatorname, int timeout){
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		return  wait.until(WaitUtil.presenceOfElementLocated(locatorname));

	}

	//Given certain number of web element to see if it is found within timeout
	public Boolean waitForMe(WebDriver driver, By locatorname, int count, int timeout) throws InterruptedException{
                long ctime = System.currentTimeMillis();

		while ((timeout*1000 > System.currentTimeMillis()- ctime)){
			List<WebElement> elementList = driver.findElements(locatorname);
			if ((elementList.size()< count)){
				Thread.sleep(200);
			}
			//element is found within timeout
			else{
                            ppcLogger.info(elementList.size()+ " elements founded!");
                            return true;
                        }
				

		}

		// otherwise element is not found within timeout
                ppcLogger.warn("No such element founded!");
		return false;
	}

	//Given certain number of web element to see if it is disappear within timeout
	public Boolean waitForMeDisappear(WebDriver driver, By locatorname, int timeout) throws InterruptedException{
		long ctime = System.currentTimeMillis();

		while ((timeout*1000 > System.currentTimeMillis()- ctime)){
			List<WebElement> elementList = driver.findElements(locatorname);
			if ((!elementList.isEmpty())){
				Thread.sleep(200);
			}
			//element is Disappear within timeout
			else
				return true;

		}

		// otherwise element is still show up within timeout
		return false;
	}

	public static Function<WebDriver, WebElement> presenceOfElementLocated(final By locator) {
		// TODO Auto-generated method stub
		return new Function<WebDriver, WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				if (driver.findElement(locator)!= null){
					return driver.findElement(locator);
				}
				else return null;
			}
		};
	}
}
