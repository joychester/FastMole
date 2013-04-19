/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stubhub;

import ch.qos.logback.classic.Logger;
import mole.helper.Wrapper;
import java.io.IOException;
import java.net.UnknownHostException;
import mole.util.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author cchi
 */
public class UKSiteBrowseCases {

    public WebDriver driver;
    public Wrapper wrapper;
    public WaitUtil myWaiter;
    
    public static final Logger myLogger = (Logger) LoggerFactory.getLogger("demo");
    
    public int timeoutval;

    @BeforeClass
    public void setUp() throws Exception{
        //Init BrowserMob Proxy Server
        myLogger.info("Before Class Setup");
        wrapper = new Wrapper();
        wrapper.initProxyServer();

        myWaiter = new WaitUtil();

        timeoutval = Integer.parseInt(Wrapper.getGlobalPara("Timeout"));
        
    }

    @AfterClass(alwaysRun=true)
    public void tearDown() throws Exception{
        // Stop any Service, Proxy Server and quit Drivers
        myLogger.info("After Class...");
        wrapper.stop();
    }

    @BeforeMethod
    public void start() throws UnknownHostException, IOException, Exception{
        // Launch browsers
        myLogger.info("Before Method...");
        driver = wrapper.launchDriver();
    }

    @AfterMethod(alwaysRun=true)
    public void stop(){
        // Quit the browser
        myLogger.info("After Method...");
        driver.quit();
    }
    
    @Test(invocationCount=1,timeOut=60000)
    public void stubHub_UK_BRX_Page() throws InterruptedException{
        myLogger.info("Kick off stubHub_UK_BRX_Page");
        //create a new har file
        wrapper.createHarFile("BRX_UK_Home_Page");

        //test steps, with whitelist and black list
        driver.get("http://www.stubhub.co.uk/");
        //Wait and Assertion

        //upload this har file to harstorage
        wrapper.pumpToHarStorage();

        //Sports page
        wrapper.createHarFile("BRX_UK_Concert_Page");

        driver.get("http://www.stubhub.co.uk/concert-tickets/");

        wrapper.pumpToHarStorage();

        //basketball page
        wrapper.createHarFile("BRX_UK_Genre_Page");

        driver.get("http://www.stubhub.co.uk/one-direction-tickets/");

        wrapper.pumpToHarStorage();

        //basketball page
        wrapper.createHarFile("BRX_UK_Event_Page");

        driver.findElement(By.cssSelector("span[itemprop=\"name performers\"]")).click();

        if(!myWaiter.waitForMe(driver, By.id("ticketsAvailable"), 1, timeoutval)) return;

        wrapper.pumpToHarStorage();

        //ticket detail page
        String ticketnumber = driver.findElement(By.id("ticketsAvailable")).findElement(By.tagName("span")).getText();

        if(Integer.parseInt(ticketnumber)>0){
            //deal with the popup window
            WebElement popup = driver.findElement(By.id("btnOk"));
            if(popup.isDisplayed()){
                popup.click();
            }

            wrapper.createHarFile("BRX_UK_Ticket_Detail_Page");

            driver.findElement(By.id("go")).click();

            wrapper.pumpToHarStorage();
        }
    }

}
