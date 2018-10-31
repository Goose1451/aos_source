package com.advantage.order.store.dto;

public class RestoreDefaultDBSettingsResponse {

    private boolean success;
    private String details;

    public RestoreDefaultDBSettingsResponse(boolean success, String details) {
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