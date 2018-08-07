package com.advantage.accountsoap.dto.account;
//Modify this class with resources/accountservice.xsd

import com.advantage.accountsoap.config.WebServiceConfig;

import javax.xml.bind.annotation.*;

/**
 * @author Binyamin Regev on on 09/05/2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "",
        namespace = WebServiceConfig.NAMESPACE_URI,
        propOrder = {
                "response",

        })
@XmlRootElement(name = "AccountPermanentDeleteResponse", namespace = WebServiceConfig.NAMESPACE_URI)
public class AccountPermanentDeleteResponse extends AccountStatusResponse {
    @XmlElement(name = "StatusMessage", namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private AccountStatusResponse response;
    @XmlElement(name = "OrderHeaderDelete", namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private boolean orderHeaderDelete;
    @XmlElement(name = "OrderLinesDelete", namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private boolean orderLinesDelete;
    @XmlElement(name = "ShippingAddressDelete", namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private boolean shippingAddressDelete;
    @XmlElement(name = "PaymentPreferenceDelete", namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private boolean paymentPreferenceDelete;
    @XmlElement(name = "AccountDelete", namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private boolean accountDelete;
    @XmlElement(name = "IsSuccess", namespace = WebServiceConfig.NAMESPACE_URI, required = true)
    private boolean isSuccess;
    @XmlElement(name = "Reason", namespace = WebServiceConfig.NAMESPACE_URI)
    private String reason;

    public AccountPermanentDeleteResponse() {
    }

    public AccountPermanentDeleteResponse(boolean orderHeaderDelete, boolean orderLinesDelete, boolean shippingAddressDelete, boolean paymentPreferenceDelete, boolean isSuccess) {
        this.orderHeaderDelete = orderHeaderDelete;
        this.orderLinesDelete = orderLinesDelete;
        this.shippingAddressDelete = shippingAddressDelete;
        this.paymentPreferenceDelete = paymentPreferenceDelete;
        this.isSuccess = isSuccess;
    }

    public AccountPermanentDeleteResponse(AccountStatusResponse response, boolean orderHeaderDelete, boolean orderLinesDelete, boolean shippingAddressDelete, boolean paymentPreferenceDelete, boolean isSuccess, String reason) {
        this.response = response;
        this.orderHeaderDelete = orderHeaderDelete;
        this.orderLinesDelete = orderLinesDelete;
        this.shippingAddressDelete = shippingAddressDelete;
        this.paymentPreferenceDelete = paymentPreferenceDelete;
        this.isSuccess = isSuccess;
        this.reason = reason;
    }
    public AccountPermanentDeleteResponse(AccountStatusResponse response, boolean orderHeaderDelete, boolean orderLinesDelete, boolean shippingAddressDelete, boolean paymentPreferenceDelete,boolean accountDelete, boolean isSuccess, String reason) {
        this.response = response;
        this.orderHeaderDelete = orderHeaderDelete;
        this.orderLinesDelete = orderLinesDelete;
        this.shippingAddressDelete = shippingAddressDelete;
        this.paymentPreferenceDelete = paymentPreferenceDelete;
        this.accountDelete = accountDelete;
        this.isSuccess = isSuccess;
        this.reason = reason;
    }

    public boolean isAccountDelete() {
        return accountDelete;
    }

    public void setAccountDelete(boolean accountDelete) {
        this.accountDelete = accountDelete;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isOrderHeaderDelete() {
        return orderHeaderDelete;
    }

    public void setOrderHeaderDelete(boolean orderHeaderDelete) {
        this.orderHeaderDelete = orderHeaderDelete;
    }

    public boolean isOrderLinesDelete() {
        return orderLinesDelete;
    }

    public void setOrderLinesDelete(boolean orderLinesDelete) {
        this.orderLinesDelete = orderLinesDelete;
    }

    public boolean isShippingAddressDelete() {
        return shippingAddressDelete;
    }

    public void setShippingAddressDelete(boolean shippingAddressDelete) {
        this.shippingAddressDelete = shippingAddressDelete;
    }

    public boolean isPaymentPreferenceDelete() {
        return paymentPreferenceDelete;
    }

    public void setPaymentPreferenceDelete(boolean paymentPreferenceDelete) {
        this.paymentPreferenceDelete = paymentPreferenceDelete;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public AccountPermanentDeleteResponse(AccountStatusResponse response) {
        this.response = response;
    }

    public AccountStatusResponse getResponse() {
        return response;
    }

    public void setResponse(AccountStatusResponse response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "AccountPermanentDeleteResponse{" +
                "response=" + response +
                ", orderHeaderDelete=" + orderHeaderDelete +
                ", orderLinesDelete=" + orderLinesDelete +
                ", shippingAddressDelete=" + shippingAddressDelete +
                ", paymentPreferenceDelete=" + paymentPreferenceDelete +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
