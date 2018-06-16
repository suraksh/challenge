package com.challenge.n26.model;

public class Transaction {

    private double amt;
    private long timestamp;

    public Transaction(double amt, long timestamp) {
        this.amt = amt;
        this.timestamp = timestamp;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
