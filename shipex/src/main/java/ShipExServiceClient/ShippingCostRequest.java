
package ShipExServiceClient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="SETransactionType" type="{https://www.AdvantageOnlineBanking.com/ShipEx/}TransactionType"/>
 *         &lt;element name="SEAddress" type="{https://www.AdvantageOnlineBanking.com/ShipEx/}SEAddress"/>
 *         &lt;element name="SENumberOfProducts" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SECustomerName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SECustomerPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "seTransactionType",
    "seAddress",
    "seNumberOfProducts",
    "seCustomerName",
    "seCustomerPhone"
})
@XmlRootElement(name = "ShippingCostRequest", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/")
public class ShippingCostRequest {

    @XmlElement(name = "SETransactionType", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    @XmlSchemaType(name = "string")
    protected TransactionType seTransactionType;
    @XmlElement(name = "SEAddress", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected SEAddress seAddress;
    @XmlElement(name = "SENumberOfProducts", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/")
    protected int seNumberOfProducts;
    @XmlElement(name = "SECustomerName", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected String seCustomerName;
    @XmlElement(name = "SECustomerPhone", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected String seCustomerPhone;

    /**
     * Gets the value of the seTransactionType property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionType }
     *     
     */
    public TransactionType getSETransactionType() {
        return seTransactionType;
    }

    /**
     * Sets the value of the seTransactionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionType }
     *     
     */
    public void setSETransactionType(TransactionType value) {
        this.seTransactionType = value;
    }

    /**
     * Gets the value of the seAddress property.
     * 
     * @return
     *     possible object is
     *     {@link SEAddress }
     *     
     */
    public SEAddress getSEAddress() {
        return seAddress;
    }

    /**
     * Sets the value of the seAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link SEAddress }
     *     
     */
    public void setSEAddress(SEAddress value) {
        this.seAddress = value;
    }

    /**
     * Gets the value of the seNumberOfProducts property.
     * 
     */
    public int getSENumberOfProducts() {
        return seNumberOfProducts;
    }

    /**
     * Sets the value of the seNumberOfProducts property.
     * 
     */
    public void setSENumberOfProducts(int value) {
        this.seNumberOfProducts = value;
    }

    /**
     * Gets the value of the seCustomerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECustomerName() {
        return seCustomerName;
    }

    /**
     * Sets the value of the seCustomerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECustomerName(String value) {
        this.seCustomerName = value;
    }

    /**
     * Gets the value of the seCustomerPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSECustomerPhone() {
        return seCustomerPhone;
    }

    /**
     * Sets the value of the seCustomerPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSECustomerPhone(String value) {
        this.seCustomerPhone = value;
    }

}
