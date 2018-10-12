package scripts.starfox.api.util.xml;

import org.w3c.dom.Element;

/**
 *
 * @author Spencer
 */
public interface XMLable {
    
    Element toXML(XMLWriter writer, Element parent, Object... data);
    
    void fromXML(XMLReader reader, String path, Object... data);
    
    String getXMLName();
}
