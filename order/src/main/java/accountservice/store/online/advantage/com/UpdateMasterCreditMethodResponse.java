
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
 *         &lt;element name="StatusMessage" form="qualified">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean" form="qualified"/>
 *                   &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" form="qualified"/>
 *                   &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "statusMessage"
})
@XmlRootElement(name = "UpdateMasterCreditMethodResponse")
public class UpdateMasterCreditMethodResponse {

    @XmlElement(name = "StatusMessage", required = true)
    protected UpdateMasterCreditMethodResponse.StatusMessage statusMessage;

    /**
     * Gets the value of the statusMessage property.
     *
     * @return possible object is
     * {@link UpdateMasterCreditMethodResponse.StatusMessage }
     */
    public UpdateMasterCreditMethodResponse.StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the value of the statusMessage property.
     *
     * @param value allowed object is
     *              {@link UpdateMasterCreditMethodResponse.StatusMessage }
     */
    public void setStatusMessage(UpdateMasterCreditMethodResponse.StatusMessage value) {
        this.statusMessage = value;
    }


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
     *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean" form="qualified"/>
     *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" form="qualified"/>
     *         &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "success",
            "id",
            "reason"
    })
    public static class StatusMessage {

        protected boolean success;
        protected long id;
        @XmlElement(required = true)
        protected String reason;

        /**
         * Gets the value of the success property.
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Sets the value of the success property.
         */
        public void setSuccess(boolean value) {
            this.success = value;
        }

        /**
         * Gets the value of the id property.
         */
        public long getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         */
        public void setId(long value) {
            this.id = value;
        }

        /**
         * Gets the value of the reason property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getReason() {
            return reason;
        }

        /**
         * Sets the value of the reason property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setReason(String value) {
            this.reason = value;
        }

    }

}
