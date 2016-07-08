
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
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="safePayUsername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="safePayPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="referenceId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="base64Token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "userId",
        "safePayUsername",
        "safePayPassword",
        "referenceId",
        "base64Token"
})
@XmlRootElement(name = "UpdateSafePayMethodRequest")
public class UpdateSafePayMethodRequest {

    protected long userId;
    @XmlElement(required = true)
    protected String safePayUsername;
    @XmlElement(required = true)
    protected String safePayPassword;
    protected long referenceId;
    @XmlElement(required = true)
    protected String base64Token;

    /**
     * Gets the value of the userId property.
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     */
    public void setUserId(long value) {
        this.userId = value;
    }

    /**
     * Gets the value of the safePayUsername property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSafePayUsername() {
        return safePayUsername;
    }

    /**
     * Sets the value of the safePayUsername property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSafePayUsername(String value) {
        this.safePayUsername = value;
    }

    /**
     * Gets the value of the safePayPassword property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSafePayPassword() {
        return safePayPassword;
    }

    /**
     * Sets the value of the safePayPassword property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSafePayPassword(String value) {
        this.safePayPassword = value;
    }

    /**
     * Gets the value of the referenceId property.
     */
    public long getReferenceId() {
        return referenceId;
    }

    /**
     * Sets the value of the referenceId property.
     */
    public void setReferenceId(long value) {
        this.referenceId = value;
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
