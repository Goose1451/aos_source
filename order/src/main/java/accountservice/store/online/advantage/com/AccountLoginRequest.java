
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
 *         &lt;element name="loginUser" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="loginPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "loginUser",
        "email",
        "loginPassword"
})
@XmlRootElement(name = "AccountLoginRequest")
public class AccountLoginRequest {

    @XmlElement(required = true)
    protected String loginUser;
    @XmlElement(required = true)
    protected String email;
    @XmlElement(required = true)
    protected String loginPassword;

    /**
     * Gets the value of the loginUser property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLoginUser() {
        return loginUser;
    }

    /**
     * Sets the value of the loginUser property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLoginUser(String value) {
        this.loginUser = value;
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
     * Gets the value of the loginPassword property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLoginPassword() {
        return loginPassword;
    }

    /**
     * Sets the value of the loginPassword property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLoginPassword(String value) {
        this.loginPassword = value;
    }

}
