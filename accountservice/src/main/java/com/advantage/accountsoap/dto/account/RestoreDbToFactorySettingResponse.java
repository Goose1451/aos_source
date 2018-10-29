
package com.advantage.accountsoap.dto.account;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="StatusMessage" form="qualified">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="success" type="{com.advantage.online.store.accountservice}responseReason" form="qualified"/>
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "statusMessage"
})
@XmlRootElement(name = "RestoreDbToFactorySettingResponse", namespace = "com.advantage.online.store.accountservice")
public class RestoreDbToFactorySettingResponse {

    @XmlElement(name = "StatusMessage", namespace = "com.advantage.online.store.accountservice", required = true)
    protected RestoreDbToFactorySettingResponse.StatusMessage statusMessage;

    /**
     * Gets the value of the statusMessage property.
     *
     * @return
     *     possible object is
     *     {@link RestoreDbToFactorySettingResponse.StatusMessage }
     *
     */
    public RestoreDbToFactorySettingResponse.StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the value of the statusMessage property.
     *
     * @param value
     *     allowed object is
     *     {@link RestoreDbToFactorySettingResponse.StatusMessage }
     *
     */
    public void setStatusMessage(RestoreDbToFactorySettingResponse.StatusMessage value) {
        this.statusMessage = value;
    }


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
     *         &lt;element name="success" type="{com.advantage.online.store.accountservice}responseReason" form="qualified"/>
     *         &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
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
        "success",
        "reason"
    })
    public static class StatusMessage {

        @XmlElement(namespace = "com.advantage.online.store.accountservice")
        protected boolean success;
        @XmlElement(namespace = "com.advantage.online.store.accountservice", required = true)
        protected String reason;

        /**
         * Gets the value of the success property.
         * 
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Sets the value of the success property.
         * 
         */
        public void setSuccess(boolean value) {
            this.success = value;
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

    }

}
