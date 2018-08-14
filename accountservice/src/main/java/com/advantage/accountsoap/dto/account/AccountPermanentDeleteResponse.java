package com.advantage.accountsoap.dto.account;
//Modify this class with resources/accountservice.xsd

import com.advantage.accountsoap.config.WebServiceConfig;

import javax.xml.bind.annotation.*;

/**
 * @author Tamir Shina 13.8.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "",
        namespace = WebServiceConfig.NAMESPACE_URI,
        propOrder = {
                "isSuccess",
                "reason"
        })
@XmlRootElement(name = "AccountPermanentDeleteResponse", namespace = WebServiceConfig.NAMESPACE_URI)
public class AccountPermanentDeleteResponse {
    @XmlElement(name = "IsSuccess", namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private boolean isSuccess;
    @XmlElement(name = "Reason", namespace = WebServiceConfig.NAMESPACE_URI)
    private String reason;

    public AccountPermanentDeleteResponse() {
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        this.isSuccess = success;
    }


}
