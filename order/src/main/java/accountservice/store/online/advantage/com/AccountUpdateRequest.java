
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
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accountId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="countryId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="stateProvince" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cityName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="zipcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="phoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accountType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="allowOffersPromotion" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="base64Token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "lastName",
        "firstName",
        "accountId",
        "countryId",
        "stateProvince",
        "cityName",
        "address",
        "zipcode",
        "phoneNumber",
        "email",
        "accountType",
        "allowOffersPromotion",
        "base64Token"
})
@XmlRootElement(name = "AccountUpdateRequest")
public class AccountUpdateRequest {

    @XmlElement(required = true)
    protected String lastName;
    @XmlElement(required = true)
    protected String firstName;
    protected long accountId;
    protected long countryId;
    @XmlElement(required = true)
    protected String stateProvince;
    @XmlElement(required = true)
    protected String cityName;
    @XmlElement(required = true)
    protected String address;
    @XmlElement(required = true)
    protected String zipcode;
    @XmlElement(required = true)
    protected String phoneNumber;
    @XmlElement(required = true)
    protected String email;
    protected int accountType;
    protected boolean allowOffersPromotion;
    @XmlElement(required = true)
    protected String base64Token;

    /**
     * Gets the value of the lastName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the firstName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the accountId property.
     */
    public long getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     */
    public void setAccountId(long value) {
        this.accountId = value;
    }

    /**
     * Gets the value of the countryId property.
     */
    public long getCountryId() {
        return countryId;
    }

    /**
     * Sets the value of the countryId property.
     */
    public void setCountryId(long value) {
        this.countryId = value;
    }

    /**
     * Gets the value of the stateProvince property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStateProvince() {
        return stateProvince;
    }

    /**
     * Sets the value of the stateProvince property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStateProvince(String value) {
        this.stateProvince = value;
    }

    /**
     * Gets the value of the cityName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Sets the value of the cityName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCityName(String value) {
        this.cityName = value;
    }

    /**
     * Gets the value of the address property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the zipcode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * Sets the value of the zipcode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setZipcode(String value) {
        this.zipcode = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the email property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the accountType property.
     */
    public int getAccountType() {
        return accountType;
    }

    /**
     * Sets the value of the accountType property.
     */
    public void setAccountType(int value) {
        this.accountType = value;
    }

    /**
     * Gets the value of the allowOffersPromotion property.
     */
    public boolean isAllowOffersPromotion() {
        return allowOffersPromotion;
    }

    /**
     * Sets the value of the allowOffersPromotion property.
     */
    public void setAllowOffersPromotion(boolean value) {
        this.allowOffersPromotion = value;
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
