/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mole.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author cchi
 */
public class XmlUtil {

    public static Map<String, String> readXmlToMap(String filename, String xpathExp, String arrtibute) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException{
        //to-do something
        Map<String, String> dataMap = new HashMap<String, String>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        
        DocumentBuilder db = dbf.newDocumentBuilder();

        File file = new File(filename);
        if(file.exists()){
            Document doc = db.parse(file);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            NodeList varList = (NodeList) xpath.evaluate(xpathExp, doc, XPathConstants.NODESET);

            for(int i=0; i<varList.getLength(); i++) {
                dataMap.put(varList.item(i).getAttributes().getNamedItem(arrtibute).getNodeValue(), varList.item(i).getTextContent());
            }
        }
        return dataMap;
    }
}
