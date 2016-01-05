package com.advantage.accountsoap.dto;

import com.advantage.accountsoap.config.WebServiceConfig;
import com.advantage.accountsoap.model.Account;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "",
        namespace = WebServiceConfig.NAMESPACE_URI,
        propOrder = {
                "account"
        })
@XmlRootElement(name = "Accounts", namespace = WebServiceConfig.NAMESPACE_URI)
public class GetAllAccountsResponse {
    @XmlElement(namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    protected List<AccountDto> account;

    public List<AccountDto> getAccount() {
        if (account == null) {
            return new ArrayList<>();
        }

        return this.account;
    }

    public void setAccount(List<AccountDto> account) {
        this.account = account;
    }
}