/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mole.ppc;

import ch.qos.logback.classic.Logger;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import org.xml.sax.SAXException;

/**
 *
 * @author cchi
 */
public class PPCEngine {

    public static final Logger ppcLogger = (Logger) LoggerFactory.getLogger("PPCLogger");
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
        // load TestNG XML file
		ppcLogger.info("loading the TestNG XML...");
        Parser parser = new Parser("test\\testng.xml");

        List<XmlSuite> suites = parser.parseToList();

        //write code to drive TestNG running
        TestNG testNG = new TestNG();

        ppcLogger.info("Adding testNG XML file");
        testNG.setXmlSuites(suites);

        ppcLogger.info("Start TestNG Test!");
        testNG.run();
        
        ppcLogger.info("Stop TestNG Test!");

    }

}
