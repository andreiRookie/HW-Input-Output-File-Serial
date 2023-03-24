import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigXmlReader {

    public static final String XML_CONFIG_DIR_NAME = "./config xml";
    public static final String SHOP_XML_FILE = "./config xml/shop.xml";

    private static final String LOAD_OPTIONS = "load";
    private static final String SAVE_OPTIONS = "save";
    private static final String LOG_OPTIONS = "log";

    public static final String ENABLED = "enabled";
    public static final String FILENAME = "fileName";
    public static final String FORMAT = "format";

    public static final String FORMAT_JSON = "json";
    public static final String FORMAT_TEXT = "text";

    private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;

    private Document document;


    public ConfigXmlReader() throws ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

    public HashMap<String, String> getLoadOptions() {

        HashMap<String, String> result = new HashMap<>();
        NodeList configNodeList = document.getChildNodes().item(0).getChildNodes();

        for (int i = 0; i < configNodeList.getLength(); i++) {
            Node node = configNodeList.item(i);
            if (node.getNodeName().equals(LOAD_OPTIONS)) {
                NodeList loadNodeList = node.getChildNodes();
                for (int j = 0; j < loadNodeList.getLength(); j++) {
                    Node _node = loadNodeList.item(j);
                    if (_node.getNodeName().equals(ENABLED)) {
                        result.put(ENABLED, _node.getTextContent());
                    } else if (_node.getNodeName().equals(FILENAME)) {
                        result.put(FILENAME, _node.getTextContent());
                    } else if (_node.getNodeName().equals(FORMAT)) {
                        result.put(FORMAT, _node.getTextContent());
                    }
                }
            }

        }
        return result;
    }

    public HashMap<String, String> getLogOptions() {

        HashMap<String, String> result = new HashMap<>();
        NodeList configNodeList = document.getChildNodes().item(0).getChildNodes();

        for (int i = 0; i < configNodeList.getLength(); i++) {
            Node node = configNodeList.item(i);
            if (node.getNodeName().equals(LOG_OPTIONS)) {
                NodeList loadNodeList = node.getChildNodes();
                for (int j = 0; j < loadNodeList.getLength(); j++) {
                    Node _node = loadNodeList.item(j);
                    if (_node.getNodeName().equals(ENABLED)) {
                        result.put(ENABLED, _node.getTextContent());
                    } else if (_node.getNodeName().equals(FILENAME)) {
                        result.put(FILENAME, _node.getTextContent());
                    }
                }
            }

        }
        return result;
    }


    public HashMap<String, String> getSaveOptions() {

        HashMap<String, String> result = new HashMap<>();
        NodeList configNodeList = document.getChildNodes().item(0).getChildNodes();

        for (int i = 0; i < configNodeList.getLength(); i++) {
            Node node = configNodeList.item(i);
            if (node.getNodeName().equals(SAVE_OPTIONS)) {
                NodeList loadNodeList = node.getChildNodes();
                for (int j = 0; j < loadNodeList.getLength(); j++) {
                    Node _node = loadNodeList.item(j);
                    if (_node.getNodeName().equals(ENABLED)) {
                        result.put(ENABLED, _node.getTextContent());
                    } else if (_node.getNodeName().equals(FILENAME)) {
                        result.put(FILENAME, _node.getTextContent());
                    } else if (_node.getNodeName().equals(FORMAT)) {
                        result.put(FORMAT, _node.getTextContent());
                    }
                }
            }

        }
        return result;
    }

    public Node getRootNode() {
        return this.document.getDocumentElement();
    }

    public Node getChildNode(Node root, String childNode) {
        Node result = null;
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i< nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()
                    && node.getNodeName().equals(childNode)) {

                result = node;
                System.out.println("result.getNodeName(): " + result.getNodeName());
                return result;
            }
        }
        return result;
    }


    public String getAttrValueByName(Node node, String attr) {
        String attrValueResult = "";
//            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                NamedNodeMap nodeMap = element.getAttributes();
        System.out.println("NamedNodeMap nodeMap:" + nodeMap.getLength());

                for (int j = 0; j < nodeMap.getLength(); j++) {

                    String attrName = nodeMap.item(j).getNodeName();
                    System.out.println("attrName: "+ attrName);
                    if (attrName.equals(attr)) {
                        attrValueResult = nodeMap.item(j).getNodeValue();
                        System.out.println("return attrValueResult:" + attrValueResult);
                        return attrValueResult;
                    }
//                }
            }
        return attrValueResult;
    }


    public String getAttrVal(Node root, String childName, String attrName) throws IOException, SAXException {
        String result = "";

        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i< nodeList.getLength(); i++) {

            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType() &&
                    node.getNodeName().equals(childName)) {

                Element element = (Element) node;

                NamedNodeMap nodeMap = element.getAttributes();

                for (int j = 0; j < nodeMap.getLength(); j++) {
                        String name = nodeMap.item(j).getNodeName();
                        String attr = nodeMap.item(j).getNodeValue();
                    if (name.equals(attrName)) {
                        result = attr;

                        return result;
                    }
                }
            }
        }
        return result;
    }

    public File makeXmlFile() {
        return new File(SHOP_XML_FILE);
    }

    public Document buildDocument(File file) {
        try {
            this.document = builder.parse(file);
        } catch (IOException|IllegalArgumentException|SAXException ex) {
            ex.printStackTrace();
        }
        return this.document;
    }

    public static void makeXmlConfigDir(String dirName) {
        File dir = new File(dirName);
        try {
            dir.mkdir();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }
}
