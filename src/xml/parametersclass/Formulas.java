//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.02.25 at 02:38:57 PM GMT-03:00 
//


package xml.parametersclass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}formula1"/>
 *         &lt;element ref="{}formula2"/>
 *         &lt;element ref="{}formula3"/>
 *         &lt;element ref="{}formula4"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "formula1",
    "formula2",
    "formula3",
    "formula4"
})
@XmlRootElement(name = "formulas")
public class Formulas {

    @XmlElement(required = true)
    protected Formula1 formula1;
    @XmlElement(required = true)
    protected Formula2 formula2;
    @XmlElement(required = true)
    protected Formula3 formula3;
    @XmlElement(required = true)
    protected Formula4 formula4;

    /**
     * Gets the value of the formula1 property.
     * 
     * @return
     *     possible object is
     *     {@link Formula1 }
     *     
     */
    public Formula1 getFormula1() {
        return formula1;
    }

    /**
     * Sets the value of the formula1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Formula1 }
     *     
     */
    public void setFormula1(Formula1 value) {
        this.formula1 = value;
    }

    /**
     * Gets the value of the formula2 property.
     * 
     * @return
     *     possible object is
     *     {@link Formula2 }
     *     
     */
    public Formula2 getFormula2() {
        return formula2;
    }

    /**
     * Sets the value of the formula2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Formula2 }
     *     
     */
    public void setFormula2(Formula2 value) {
        this.formula2 = value;
    }

    /**
     * Gets the value of the formula3 property.
     * 
     * @return
     *     possible object is
     *     {@link Formula3 }
     *     
     */
    public Formula3 getFormula3() {
        return formula3;
    }

    /**
     * Sets the value of the formula3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Formula3 }
     *     
     */
    public void setFormula3(Formula3 value) {
        this.formula3 = value;
    }

    /**
     * Gets the value of the formula4 property.
     * 
     * @return
     *     possible object is
     *     {@link Formula4 }
     *     
     */
    public Formula4 getFormula4() {
        return formula4;
    }

    /**
     * Sets the value of the formula4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Formula4 }
     *     
     */
    public void setFormula4(Formula4 value) {
        this.formula4 = value;
    }

}