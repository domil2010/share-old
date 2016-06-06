package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;default-controls&gt; elements.
 * 
 * @author Neil McErlean.
 */
class DefaultControlsElementReader implements ConfigElementReader
{
    public static final String ELEMENT_DEFAULT_CONTROLS = "default-controls";
    public static final String ELEMENT_CONTROL_PARAM = "control-param";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_TEMPLATE = "template";

    /**
     * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
     */
    @SuppressWarnings("unchecked")
    public ConfigElement parse(Element defaultCtrlsElem)
    {
        DefaultControlsConfigElement result = null;
        if (defaultCtrlsElem == null)
        {
            return null;
        }

        String name = defaultCtrlsElem.getName();
        if (!name.equals(ELEMENT_DEFAULT_CONTROLS))
        {
            throw new ConfigException(this.getClass().getName()
                    + " can only parse " + ELEMENT_DEFAULT_CONTROLS
                    + " elements, the element passed was '" + name + "'");
        }

        result = new DefaultControlsConfigElement();

        // There is an assumption here that all children are <type> elements.
        Iterator<Element> typeNodes = defaultCtrlsElem.elementIterator();
        while (typeNodes.hasNext())
        {
            Element nextTypeNode = typeNodes.next();
            String typeName = nextTypeNode.attributeValue(ATTR_NAME);
            String templatePath = nextTypeNode.attributeValue(ATTR_TEMPLATE);

            List<Element> controlParamNodes = nextTypeNode.elements(ELEMENT_CONTROL_PARAM);
            ControlParam param = null;
            // If the optional control-param tags are present
            List<ControlParam> params = new ArrayList<ControlParam>();

            for (Element nextControlParam : controlParamNodes)
            {
                String paramName = nextControlParam.attributeValue(ATTR_NAME);
                String elementValue = nextControlParam.getTextTrim();
                // This impl assumes a String value within the control-param tags.
                // Cannot handle a value as XML attribute.
                param = new ControlParam(paramName, elementValue);
                params.add(param);
            }
            
            result.addDataMapping(typeName, templatePath, params);
        }

        return result;
    }
}
