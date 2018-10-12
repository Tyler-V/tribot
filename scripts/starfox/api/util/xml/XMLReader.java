package scripts.starfox.api.util.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import scripts.starfox.api.Client;
import scripts.starfox.api.Printing;

/**
 *
 * @author Spencer
 */
public class XMLReader {

    private final XPath xPath;
    private final Document doc;
    private final XMLable xml;

    public XMLReader(final XMLable xml, final File file) {
        this.xml = xml;
        this.xPath = XPathFactory.newInstance().newXPath();
        this.doc = getDocument(file);
    }

    private Document getDocument(File file) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        } catch (ParserConfigurationException | IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            Printing.dev("Error loading file. If this is the first time the file is being loaded, ignore this.");
        } catch (Exception ex) {
            Printing.dev("Regular Exception Thrown.");
            ex.printStackTrace();
        }
        return null;
    }

    public final int readCount(String name) {
        return evalGroupCount("/", "base", name);
    }

    public final XMLable read(Object... data) {
        xml.fromXML(this, "/", data);
        return xml;
    }

    public final XMLable readGroup(int index, Object... data) {
        xml.fromXML(this, "/base/" + xml.getXMLName() + "[" + (index + 1) + "]/", data);
        return xml;
    }

    /**
     * Evaluates a single element from the specified path and name.
     *
     * @param name The name.
     * @param path The path.
     * @return The value of the element at the end of the specified path with the specified name.
     */
    public final String eval(String name, String path) {
        return evaluate(path + name);
    }

    /**
     * Evaluates a single element from the specified path and name.
     *
     * @param name The name.
     * @param path The path.
     * @return The value of the element at the end of the specified path with the specified name.
     */
    public final int evalInt(String name, String path) {
        try {
            return Integer.parseInt(evaluate(path + name));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Evaluates a single element from the specified path and name.
     *
     * @param name The name.
     * @param path The path.
     * @return The value of the element at the end of the specified path with the specified name.
     */
    public final long evalLong(String name, String path) {
        try {
            return Long.parseLong(evaluate(path + name));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Evaluates a single element from the specified path and name.
     *
     * @param name The name.
     * @param path The path.
     * @return The value of the element at the end of the specified path with the specified name.
     */
    public final boolean evalBool(String name, String path) {
        return Boolean.parseBoolean(evaluate(path + name));
    }

    /**
     * Returns an ArrayList of Strings that represent the elements inside the specified path, with the specified name.
     *
     * @param path      The path.
     * @param groupName The name of the group.
     * @param name      The name of the element.
     * @return An ArrayList of Strings that represent the elements inside the specified path, with the specified name.
     */
    public final ArrayList<String> evalArray(String path, String groupName, String name) {
        final ArrayList<String> evals = new ArrayList<>();
        int count = evalGroupCount(path, groupName, name);
        for (int i = 1; i <= count; i++) {
            evals.add(evaluate(getGroupElement(path, groupName, name, i)));
        }
        return evals;
    }

    /**
     * Returns an ArrayList of paths that represent elements that have yet to be evaluated.
     *
     * Once this method returns, {@link XMLable#fromXML(XMLReader, String)} should be used on a new instance of the object that each path in the returned ArrayList represents.
     *
     * @param path      The previously defined path.
     * @param groupName The name of the group.
     * @param name      The name of the element.
     * @return An ArrayList of paths that represent elements that have yet to be evaluated.
     */
    public final ArrayList<String> getArrayPaths(String path, String groupName, String name) {
        final ArrayList<String> evals = new ArrayList<>();
        int count = evalGroupCount(path, groupName, name);
        for (int i = 1; i <= count; i++) {
            evals.add(getGroupElement(path, groupName, name, i) + "/");
        }
        return evals;
    }

    private String getGroupElement(String path, String groupName, String name, int index) {
        return getGroupPath(path, groupName, name) + "[" + index + "]";
    }

    private int evalGroupCount(String path, String group, String name) {
        try {
            return Integer.parseInt(evaluate("count(" + getGroupPath(path, group, name) + ")"));
        } catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    private String getGroupPath(String path, String groupName, String groupElement) {
        return path + groupName + "/" + groupElement;
    }

    private String evaluate(String path) {
        try {
            return xPath.evaluate(path, doc);
        } catch (XPathExpressionException ex) {
            Client.println("Failed to evaluate.");
        }
        return null;
    }
}
