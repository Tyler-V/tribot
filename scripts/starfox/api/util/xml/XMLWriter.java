package scripts.starfox.api.util.xml;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Spencer
 */
public class XMLWriter {

    private final Document doc;
    private final XMLable[] xmls;

    public XMLWriter(final XMLable... xml) {
        this.xmls = xml;
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        if (builder != null) {
            doc = builder.newDocument();
        } else {
            throw new SigmaXMLException("Builder was null.");
        }
    }

    public final void save(final File file, boolean isArray, Object... data) {
        Element base;
        if (!isArray) {
             base = xmls[0].toXML(this, doc.createElement(xmls[0].getXMLName()), data);
        } else {
            base = doc.createElement("base");
            for (XMLable xmlAble : xmls) {
                base.appendChild(xmlAble.toXML(this, doc.createElement(xmlAble.getXMLName()), data));
            }
        }
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc.appendChild(base)), new StreamResult(file));
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Appends a new Element with the specified name containing the specified value onto the specified parent Element.
     *
     * @param parent The parent Element.
     * @param name   The name of the new Element.
     * @param value  The value contained within the new Element.
     */
    public final void append(Element parent, String name, String value) {
        Element e = doc.createElement(name);
        e.appendChild(doc.createTextNode(value));
        parent.appendChild(e);
    }

    /**
     * Appends a new Element with the specified name containing the specified value onto the specified parent Element.
     *
     * @param parent The parent Element.
     * @param name   The name of the new Element.
     * @param value  The value contained within the new Element.
     */
    public final void append(Element parent, String name, long value) {
        append(parent, name, "" + value);
    }

    /**
     * Appends a new Element with the specified name containing the specified value onto the specified parent Element.
     *
     * @param parent The parent Element.
     * @param name   The name of the new Element.
     * @param value  The value contained within the new Element.
     */
    public final void append(Element parent, String name, boolean value) {
        append(parent, name, "" + value);
    }

    /**
     * Appends the XMLable to the specified parent Element.
     *
     * @param parent The parent Element.
     * @param xml    The XMLable.
     */
    public final void append(Element parent, XMLable xml) {
        parent.appendChild(xml.toXML(this, doc.createElement(xml.getXMLName())));
    }

    /**
     * Appends all of the non-XMLables in the specified ArrayList onto the specified element. All appended elements are contained within a new Element with the specified name.
     *
     * This method is incredibly useful for saving ArrayLists of objects that you don't have access to, such as Strings, Points, etc. Otherwise, it is usually a better idea to use
     * the {@code #appendArray(Element, String, ArrayList<T>, XMLLoader<T>} method instead.
     *
     * @param <T>    The type of non-XMLable.
     * @param parent The parent Element.
     * @param name   The name of the new element group.
     * @param arr    The ArrayList.
     * @param loader The XMLLoader that will be used to convert the specified non-XMLables into XMLables.
     */
    public final <T> void appendArray(Element parent, String name, ArrayList<T> arr, XMLLoader<T> loader) {
        Element e = doc.createElement(name);
        for (T t : arr) {
            XMLable xml = loader.toXMLable(t);
            e.appendChild(xml.toXML(this, doc.createElement(xml.getXMLName())));
        }
        parent.appendChild(e);
    }

    /**
     * Appends all of the XMLables in the specified ArrayList onto the specified element. All appended elements are contained within a new Element with the specified name.
     *
     * @param <T>    The type of XMLable.
     * @param parent The parent Element.
     * @param name   The name of the new element group.
     * @param arr    The ArrayList.
     */
    public final <T extends XMLable> void appendArray(Element parent, String name, ArrayList<T> arr) {
        Element e = doc.createElement(name);
        for (T t : arr) {
            e.appendChild(t.toXML(this, doc.createElement(t.getXMLName())));
        }
        parent.appendChild(e);
    }

    /**
     * A class that is useful for converting non-XMLable classes into an XMLable.
     *
     * @param <T> The type of object that this XMLLoader is loading.
     */
    public interface XMLLoader<T> {

        public <T> XMLable toXMLable(T t);
    }
}
