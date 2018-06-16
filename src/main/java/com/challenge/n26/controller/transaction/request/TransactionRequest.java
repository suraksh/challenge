package com.challenge.n26.controller.transaction.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

public class TransactionRequest {

    private static final String AMOUNT_EMPTY = "Amount cannot be empty";
    private static final String TIMESTAMP_EMPTY = "Timestamp cannot be empty";

    @ApiModelProperty(position = 0, required = true, value = "12.3", example = "12.3")
    @NotNull(message = AMOUNT_EMPTY)
    private Double amount;

    @ApiModelProperty(position = 1, required = true, value = "1478192204000", example = "1478192204000")
    @NotNull(message = TIMESTAMP_EMPTY)
    private long timestamp;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
