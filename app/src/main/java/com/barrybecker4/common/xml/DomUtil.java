/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Static utility methods for manipulating an XML dom.
 *
 * @author Barry Becker
 */
public final class DomUtil {

    private DomUtil() {}

    /** This URL is where I keep all my published xsds (xml schemas) and dtds (doc type definitions) */
    private static final String SCHEMA_LOCATION = "http://barrybecker4.com/schema/"; //NON-NLS

    private static final String ROOT_ELEMENT = "rootElement";  //NON-NLS
    private static final String USE_ELEMENT = "use";  //NON-NLS

    /**
     * Initialize a dom document structure.
     *
     * @return dom Document
     */
    public static Document buildDom() {
        Document document = null;
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();  // Create from whole cloth

            Element root = document.createElement(ROOT_ELEMENT);
            document.appendChild(root);

            // normalize text representation
            // getDocumentElement() returns the document's root node
            document.getDocumentElement().normalize();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        }
        return document;
    }

    /**
     * @return a new document (or null if there was an error creating one)
     */
    public static Document createNewDocument() {
        DocumentBuilderFactory documentBuilderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.newDocument();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DomUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Go through the dom hierarchy and remove spurious text nodes and also
     * replace "use" nodes with a deep copy of what they refer to.
     *
     * @param root     root of document
     * @param document the xml document
     */
    private static void postProcessDocument(Node root, Document document, boolean replaceUseWithDeepCopy) {
        NodeList l = root.getChildNodes();

        List<Node> deleteList = new ArrayList<>();

        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);
            String name = n.getNodeName();
            if (name != null && name.startsWith("#text")) {
                // delete if nothing by whitespace
                String text = n.getNodeValue();
                if (text.matches("[ \\t\n\\x0B\\f\\r]*")) {
                    deleteList.add(n);
                }
            }

            postProcessDocument(n, document, replaceUseWithDeepCopy);

            if (name != null && USE_ELEMENT.equals(name)) {
                // substitute the element with the specified id
                NamedNodeMap attrs = n.getAttributes();
                Node attr = attrs.item(0);
                assert "ref".equals(attr.getNodeName()) : "attr name=" + attr.getNodeName();
                String attrValue = attr.getNodeValue();

                Node element = document.getElementById(attrValue);
                Node clonedElement = element.cloneNode(replaceUseWithDeepCopy);

                // Still need to recursively clean the node that was replaced
                // since it might also contain use nodes.
                postProcessDocument(clonedElement, document, replaceUseWithDeepCopy);

                root.replaceChild(clonedElement, n);
            }
        }

        for (Node aDeleteList : deleteList) {
            root.removeChild(aDeleteList);
        }
    }

    /**
     * get the value for an attribute.
     * Error if the attribute does not exist.
     *
     * @param node       node to get attribute from
     * @param attribName attribute to get
     */
    public static String getAttribute(Node node, String attribName) {

        String attributeVal = getAttribute(node, attribName, null);
        assert (attributeVal != null) :
                "no attribute named '" + attribName + "' for node '" + node.getNodeName() + "' val=" + node.getNodeValue();
        return attributeVal;
    }

    /**
     * get the value for an attribute. If not found, defaultValue is used.
     *
     * @param node         node to get attribute on
     * @param attribName   name of attribute to get
     * @param defaultValue default to use if not there
     */
    public static String getAttribute(Node node, String attribName, String defaultValue) {
        NamedNodeMap attribMap = node.getAttributes();
        String attributeVal = null;
        if (attribMap == null) {
            return null;
        }

        for (int i = 0; i < attribMap.getLength(); i++) {
            Node attr = attribMap.item(i);
            if (attr.getNodeName().equals(attribName))
                attributeVal = attr.getNodeValue();
        }
        if (attributeVal == null)
            attributeVal = defaultValue;

        return attributeVal;
    }

    /**
     * a concatenated list of the node's attributes.
     *
     * @param attributeMap maps names to nodes
     * @return list of attributes
     */
    public static String getAttributeList(NamedNodeMap attributeMap) {
        String attribs = "";
        if (attributeMap != null) {
            attributeMap.getLength();

            for (int i = 0; i < attributeMap.getLength(); i++) {
                Node n = attributeMap.item(i);
                attribs += n.getNodeName() + "=\"" + n.getNodeValue() + "\"  ";
            }
        }
        return attribs;
    }

    /**
     * print a text representation of the dom hierarchy.
     *
     * @param root  document root node
     * @param level level to print to
     */
    public static void printTree(Node root, int level) {
        NodeList l = root.getChildNodes();
        for (int i = 0; i < level; i++)
            System.out.print("    ");

        NamedNodeMap attribMap = root.getAttributes();
        String attribs = getAttributeList(attribMap);

        System.out.println("Node: <" + root.getNodeName() + ">  " + attribs);
        for (int i = 0; i < l.getLength(); i++) {
            printTree(l.item(i), level + 1);
        }
    }

    /**
     * Parse an xml file and return a cleaned up Document object.
     * Set replaceUseWithDeepCopy to false if you are in a debug mode and don't want to see a lot of redundant subtrees.
     *
     * @param stream                 some input stream.
     * @param replaceUseWithDeepCopy if true then replace each instance of a use node with a deep copy of
     *                               what it refers to
     * @param xsdUri                 location of the schema to use for validation.
     * @return the parsed file as a Document
     */
    private static Document parseXML(InputStream stream, boolean replaceUseWithDeepCopy, String xsdUri) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);

        try {
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            if (xsdUri != null) {
                factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                        "http://www.w3.org/2001/XMLSchema");
                factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", xsdUri);
            }

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new XmlErrorHandler());

            document = builder.parse(stream);

            postProcessDocument(document, document, replaceUseWithDeepCopy);
            //printTree(document, 0);

        } catch (SAXException sxe) {
            // Error generated during parsing)
            Exception x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            x.printStackTrace();
        } catch (ParserConfigurationException | IOException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        }

        return document;
    }

    /**
     * @param url url that points to the xml document to parse
     * @return parsed Document
     */
    public static Document parseXML(URL url) {
        try {
            //System.out.println("url path=" + url.getPath());
            URLConnection urlc = url.openConnection();
            InputStream is = urlc.getInputStream();
            return parseXML(is, true, null);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to open " + url.getPath());
        }
    }

    /**
     * @param file the file to parse
     * @return parsed Document
     */
    public static Document parseXMLFile(File file) {
        //System.out.println("about to parse "+ file.getPath());
        return parseXMLFile(file, true);
    }

    /**
     * @param file                   the file to parse
     * @param replaceUseWithDeepCopy if true, replace element references with deep copies.
     * @return the xml document DOM object
     */
    public static Document parseXMLFile(File file, boolean replaceUseWithDeepCopy) {
        try {
            FileInputStream str = new FileInputStream(file);
            return parseXML(str, replaceUseWithDeepCopy, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Write out the xml document to a file.
     *
     * @param destinationFileName file to write xml to
     * @param document            xml document to write.
     * @param schema              of the schema to use if any (e.g. script.dtd of games.xsd). May be null.
     */
    public static void writeXMLFile(String destinationFileName, Document document, String schema) {
        OutputStream output;
        try {
            output = new BufferedOutputStream(new FileOutputStream(destinationFileName));
            writeXML(output, document, schema);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DomUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param oStream  stream to write xml to.
     * @param document the xml document to be written to the specified output stream
     * @param schema   of the schema to use if any (e.g. script.dtd of games.xsd). May be null.
     */
    private static void writeXML(OutputStream oStream, Document document, String schema) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //NON-NLS
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //NON-NLS
            if (schema != null) {
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DomUtil.SCHEMA_LOCATION + schema);
            }
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(DomUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        DOMSource source = new DOMSource(document);

        // takes some OutputStream or Writer
        StreamResult result = new StreamResult(oStream);  // replace out with FileOutputStream

        assert transformer != null;
        try {
            // replace out with FileOutputStream  // System.out
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getLogger(DomUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
