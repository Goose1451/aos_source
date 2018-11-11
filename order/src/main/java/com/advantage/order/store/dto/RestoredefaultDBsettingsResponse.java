package com.advantage.order.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestoredefaultDBsettingsResponse {

    @JsonProperty("success")
    private boolean success;
    @JsonProperty("details")
    private String details;

    public RestoredefaultDBsettingsResponse(boolean success, String details) {
        this.success = success;
        this.details = details;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "RestoredefaultDBsettingsResponse{" +
                "success=" + success +
                ", details='" + details + '\'' +
                '}';
    }
}