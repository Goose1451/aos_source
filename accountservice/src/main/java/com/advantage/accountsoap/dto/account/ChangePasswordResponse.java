package com.advantage.accountsoap.dto.account;

import com.advantage.accountsoap.config.WebServiceConfig;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "",
        namespace = WebServiceConfig.NAMESPACE_URI,
        propOrder = {
                "response"
        })
@XmlRootElement(name = "ChangePasswordResponse", namespace = WebServiceConfig.NAMESPACE_URI)
public class ChangePasswordResponse {
    @XmlElement(name = "StatusMessage", namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private AccountStatusResponse response;

    public ChangePasswordResponse() {
    }

    public ChangePasswordResponse(AccountStatusResponse response) {
        this.response = response;
    }

    public AccountStatusResponse getResponse() {
        return response;
    }

    public void setResponse(AccountStatusResponse response) {
        this.response = response;
    }
}