
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
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Reason" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TransactionReference" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TransactionDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "code",
    "reason",
    "transactionReference",
    "transactionDate"
})
@XmlRootElement(name = "PlaceShippingOrderResponse", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/")
public class PlaceShippingOrderResponse {

    @XmlElement(name = "SETransactionType", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    @XmlSchemaType(name = "string")
    protected PlaceOrderTransactionType seTransactionType;
    @XmlElement(name = "Code", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected String code;
    @XmlElement(name = "Reason", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected String reason;
    @XmlElement(name = "TransactionReference", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected String transactionReference;
    @XmlElement(name = "TransactionDate", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", required = true)
    protected String transactionDate;

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
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Gets the value of the transactionReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionReference() {
        return transactionReference;
    }

    /**
     * Sets the value of the transactionReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionReference(String value) {
        this.transactionReference = value;
    }

    /**
     * Gets the value of the transactionDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the value of the transactionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionDate(String value) {
        this.transactionDate = value;
    }

}
