
package ShippingExpresss.WsModel;

import javax.annotation.Generated;
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
 *         &lt;element name="track" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "track"
})
@XmlRootElement(name = "TrackNumberResponse", namespace = "http://advantage.store/shipex/service")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-14T03:37:12+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class TrackNumberResponse {

    @XmlElement(namespace = "http://advantage.store/shipex/service", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-14T03:37:12+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String track;

    /**
     * Gets the value of the track property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-14T03:37:12+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getTrack() {
        return track;
    }

    /**
     * Sets the value of the track property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-14T03:37:12+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setTrack(String value) {
        this.track = value;
    }

}
