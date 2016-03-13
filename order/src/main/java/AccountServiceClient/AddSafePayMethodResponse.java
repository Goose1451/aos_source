
package AccountServiceClient;

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
 *         &lt;element name="StatusMessage" type="{com.advantage.online.store.accountservice}PaymentPreferencesStatusResponse"/>
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
    "statusMessage"
})
@XmlRootElement(name = "AddSafePayMethodResponse")
public class AddSafePayMethodResponse {

    @XmlElement(name = "StatusMessage", required = true)
    protected PaymentPreferencesStatusResponse statusMessage;

    /**
     * Gets the value of the statusMessage property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentPreferencesStatusResponse }
     *     
     */
    public PaymentPreferencesStatusResponse getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the value of the statusMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentPreferencesStatusResponse }
     *     
     */
    public void setStatusMessage(PaymentPreferencesStatusResponse value) {
        this.statusMessage = value;
    }

}