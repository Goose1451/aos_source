
package accountservice.store.online.advantage.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="address" type="{com.advantage.online.store.accountservice}UpdateAddressDto"/>
 *         &lt;element name="base64Token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "address",
        "base64Token"
})
@XmlRootElement(name = "AddressUpdateRequest")
public class AddressUpdateRequest {

    @XmlElement(required = true)
    protected UpdateAddressDto address;
    @XmlElement(required = true)
    protected String base64Token;

    /**
     * Gets the value of the address property.
     *
     * @return possible object is
     * {@link UpdateAddressDto }
     */
    public UpdateAddressDto getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     *
     * @param value allowed object is
     *              {@link UpdateAddressDto }
     */
    public void setAddress(UpdateAddressDto value) {
        this.address = value;
    }

    /**
     * Gets the value of the base64Token property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBase64Token() {
        return base64Token;
    }

    /**
     * Sets the value of the base64Token property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBase64Token(String value) {
        this.base64Token = value;
    }

}
