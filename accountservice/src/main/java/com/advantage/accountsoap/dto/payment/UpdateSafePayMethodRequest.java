package com.advantage.accountsoap.dto.payment;

import com.advantage.accountsoap.config.WebServiceConfig;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "safePayUsername",
        "referenceId"

})
@XmlRootElement(name = "UpdateSafePayMethodRequest", namespace = WebServiceConfig.NAMESPACE_URI)
public class UpdateSafePayMethodRequest {
    @XmlElement(namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private String safePayUsername;
    @XmlElement(namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private long referenceId;

    public UpdateSafePayMethodRequest() {
    }

    public UpdateSafePayMethodRequest(String safePayUsername, long referenceId) {
        this.safePayUsername = safePayUsername;
        this.referenceId = referenceId;
    }

    public String getSafePayUsername() {
        return safePayUsername;
    }

    public void setSafePayUsername(String safePayUsername) {
        this.safePayUsername = safePayUsername;
    }

    public long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }
}
