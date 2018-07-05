
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
 *         &lt;element name="SETransactionType" type="{https://www.AdvantageOnlineBanking.com/ShipEx/}PlaceOrderTransactionType"/>
 *         &lt;element name="SEAddress" type="{https://www.AdvantageOnlineBanking.com/ShipEx/}SEAddress"/>
 *         &lt;element name="SEProducts" type="{https://www.AdvantageOnlineBanking.com/ShipEx/}SEProducts" form="qualified"/>
 *         &lt;element name="OrderNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "seProducts",
    "orderNumber",
    "seCustomerName",
    "seCustomerPhone"
})
@XmlRootElement(name = "PlaceShippingOrderRequest", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/")
public class PlaceShippingOrderRequest {

    @XmlElement(name = "SETransactionType", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    @XmlSchemaType(name = "string")
    protected PlaceOrderTransactionType seTransactionType;
    @XmlElement(name = "SEAddress", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected SEAddress seAddress;
    @XmlElement(name = "SEProducts", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected SEProducts seProducts;
    @XmlElement(name = "OrderNumber", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected String orderNumber;
    @XmlElement(name = "SECustomerName", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected String seCustomerName;
    @XmlElement(name = "SECustomerPhone", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected String seCustomerPhone;

    /**
     * Gets the value of the seTransactionType property.
     * 
     * @return
     *     possible object is
     *     {@link PlaceOrderTransactionType }
     *     
     */
    public PlaceOrderTransactionType getSETransactionType() {
        return seTransactionType;
    }

    /**
     * Sets the value of the seTransactionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlaceOrderTransactionType }
     *     
     */
    public void setSETransactionType(PlaceOrderTransactionType value) {
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
     * Gets the value of the seProducts property.
     * 
     * @return
     *     possible object is
     *     {@link SEProducts }
     *     
     */
    public SEProducts getSEProducts() {
        return seProducts;
    }

    /**
     * Sets the value of the seProducts property.
     * 
     * @param value
     *     allowed object is
     *     {@link SEProducts }
     *     
     */
    public void setSEProducts(SEProducts value) {
        this.seProducts = value;
    }

    /**
     * Gets the value of the orderNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the value of the orderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderNumber(String value) {
        this.orderNumber = value;
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
