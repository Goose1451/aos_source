package com.advantage.order.store.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderHistoryRemoveDto {

    @JsonProperty("successfulOrderHeaderDelete ")
    private boolean successfulOrderHeaderDelete;

    @JsonProperty("successfulOrderLineDelete ")
    private boolean successfulOrderLineDelete;

    @JsonProperty("isSuccess ")
    private boolean isSuccess;

    public OrderHistoryRemoveDto() {
    }

    public OrderHistoryRemoveDto(boolean successfulOrderHeaderDelete, boolean successfulOrderLineDelete,
                                 boolean isSuccess){

        this.successfulOrderHeaderDelete = successfulOrderHeaderDelete;

        this.successfulOrderLineDelete = successfulOrderLineDelete;

        this.isSuccess = isSuccess;
    }

    public boolean isSuccessfulOrderHeaderDelete() {
        return successfulOrderHeaderDelete;
    }

    public void setSuccessfulOrderHeaderDelete(boolean successfulOrderHeaderDelete) {
        this.successfulOrderHeaderDelete = successfulOrderHeaderDelete;
    }

    public boolean isSuccessfulOrderLineDelete() {
        return successfulOrderLineDelete;
    }

    public void setSuccessfulOrderLineDelete(boolean successfulOrderLineDelete) {
        this.successfulOrderLineDelete = successfulOrderLineDelete;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
