/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mole.helper;

import java.io.IOException;
import java.net.UnknownHostException;
import mole.util.XmlUtil;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.browsermob.core.har.Har;
import org.browsermob.proxy.ProxyServer;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cchi
 */
public class Wrapper {

    public static ProxyServer mobServer;
    private WebDriver driver = null;
    private String pageName;
    
    public static String globalParaFile = "conf\\globalPara.xml";
    public static Map<String, String> globalMap;

    private static ChromeDriverService service = null;

    static final Logger ppcLogger = (Logger) LoggerFactory.getLogger("PPCLogger");
    static String chromeDriverPath;
    static String ieDriverPath;
    static String wlist;
    static String blist;
    
    public WebDriver getDriver(){
        return driver;
    }

    public void initProxyServer() throws Exception{
        ppcLogger.info("Start the BrowserMob Proxy Server");
        //to prepare server and native driver under test
        mobServer = new ProxyServer(4444);
        
        mobServer.start();

        ppcLogger.info("Loading the Global Parameters in globalPara.xml");
        //setup global parameters xml file
	globalMap = XmlUtil.readXmlToMap(globalParaFile, "//var", "name");

        //create whitelist and blacklist, only loading the essential resources to speed up the tests
	wlist = globalMap.get("whitelist");
        blist = globalMap.get("blacklist");
        
        if(!wlist.isEmpty()){
            ppcLogger.debug("whitelist added: " + wlist);
            String[] whitelists = wlist.split(";");
            mobServer.whitelistRequests(whitelists, 200);
        } else {
            if(!blist.isEmpty()){
                ppcLogger.debug("whitelist added: " + blist);
                String[] blacklists = blist.split(";");
                for(int i=0; i< blacklists.length;i++){
                    mobServer.blacklistRequests(blacklists[i], 200);
                }
            }
        }

        // Start Chrome Driver Service if test driver is Chrome
        chromeDriverPath = globalMap.get("chromedriverpath");

        if(isChromeDriver()){
            ppcLogger.info("build Chrome Driver service");

            service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(chromeDriverPath))
                .usingAnyFreePort()
                .build();
            service.start();

            ppcLogger.info("service is running now");
        }

        ieDriverPath = globalMap.get("iedriverpath");

        ppcLogger.info("Proxy Server Started!");
            
    }

    public WebDriver launchDriver() throws UnknownHostException, IOException, Exception{
        
        Proxy proxy = mobServer.seleniumProxy();

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability(CapabilityType.PROXY, proxy);

        //start firefox driver
        if(isFirefoxDriver()){
            ppcLogger.info("Start the Firefox Driver");
            driver = new FirefoxDriver(capabilities);
        }
        //start chrome driver
        else if(isChromeDriver()){
            if(service.isRunning()){
                ppcLogger.info("Start the Chrome Driver");

                capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"));
                driver = new ChromeDriver(service,capabilities);
            }
            else{
                ppcLogger.info("Start the Chrome Service and init Driver");

                service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(chromeDriverPath))
                .usingAnyFreePort()
                .build();
                service.start();

                capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"));
                driver = new ChromeDriver(service,capabilities);
            }
        }
        //Launch IE Driver, need to enable all zones to protected mode on IE
        else if (isIEDriver()){
            ppcLogger.info("Start IE Driver");
            File file = new File(ieDriverPath);
            System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
            ppcLogger.info("Set up system property!");
            driver =  new InternetExplorerDriver(capabilities);
            ppcLogger.info("IE is opening");
        }
        else {
            // exit due to no defined driver find in global property
            ppcLogger.error("No proper driver defined, please use firefox or chrome as your native driver");
            mobServer.stop();
            System.exit(2);
        }
        ppcLogger.info("Driver init Finished!");
        return driver;
    }

    public void stop() throws Exception{

        //Make sure the driver has been quit, quit() can be call multiple times
        driver.quit();

        //check if test is on Chrome driver, then stop the service if it is still running
        if(isChromeDriver()){
            if (service.isRunning()){
            	service.stop(); 
            }	
        }

        mobServer.stop();
        
        ppcLogger.info("Proxy Server stopped and driver is closed!");
    }

    public void createHarFile(String tag){
        //set pageName
        this.pageName = tag;
        
        mobServer.newHar(pageName);

        ppcLogger.info("Ready to capture the har file for " + pageName);
    }
    
    public void pumpToHarStorage() throws InterruptedException{
        try {
            ppcLogger.info("Start to pump har file: " + pageName);
            String mongoHost = globalMap.get("harstoragehost");

            File file = writeHarToFile(pageName);
            
            //Post to mongo server which stores Har file
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(mongoHost + "/results/upload");

            //prepare post body: file=har
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("file", new FileBody(file));
            post.setEntity(entity);

            //send post request to mongo server
            client.execute(post);
            ppcLogger.info("Upload Har file to harstroage Server!");

            //Delete the har file after send the body to harstorage server
            if(file.exists()){
                ppcLogger.info(file.getAbsolutePath());
                file.delete();
            }

        } catch (IOException ex) {
            ppcLogger.error(ex.getMessage());
        }
    }

    public boolean isFirefoxDriver(){
        
        if(globalMap.get("browser").trim().equalsIgnoreCase("Firefox")){
            return true;
        }
        return false;
    }

    public boolean isChromeDriver(){

        if(globalMap.get("browser").trim().equalsIgnoreCase("Chrome")){
            return true;
        }
        return false;
    }

    public boolean isIEDriver(){
        
        if(globalMap.get("browser").trim().equalsIgnoreCase("IE")){
            return true;
        }
        return false;
    }

    public File writeHarToFile(String pageName) throws IOException, InterruptedException{
        Har har = mobServer.getHar();

        Thread.sleep(3000);

        File file = new File(pageName + ".har");

        har.writeTo(file);

        return file;
    }

    public void saveAsHarFile(String filename) throws InterruptedException, IOException{
        Har har = mobServer.getHar();

	Thread.sleep(1500);

	har.writeTo(new File(filename + ".har"));
    }
	
	public static String getGlobalPara(String paraname){
        return globalMap.get(paraname);
    }
}
