//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.12.02 at 05:44:20 PM GMT-03:00 
//
package xml.benchmarkclass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for bench element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="bench">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}name"/>
 *           &lt;element ref="{}path"/>
 *           &lt;element ref="{}wires"/>
 *           &lt;element ref="{}nodes"/>
 *         &lt;/sequence>
 *         &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "path",
    "wires",
    "nodes"
})
@XmlRootElement(name = "bench")
public class Bench {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String path;
    protected int wires;
    protected int nodes;
    @XmlAttribute(required = true)
    protected int id;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the wires property.
     * 
     */
    public int getWires() {
        return wires;
    }

    /**
     * Sets the value of the wires property.
     * 
     */
    public void setWires(int value) {
        this.wires = value;
    }

    /**
     * Gets the value of the nodes property.
     * 
     */
    public int getNodes() {
        return nodes;
    }

    /**
     * Sets the value of the nodes property.
     * 
     */
    public void setNodes(int value) {
        this.nodes = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }
}
