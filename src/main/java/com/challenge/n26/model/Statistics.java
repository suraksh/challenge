package com.challenge.n26.model;

public class Statistics {

    private double sum;
    private double avg;
    private double max;
    private double min;
    private long count;
    private long timestamp;

    public Statistics() {

    }

    public double getSum() {
        return sum;
    }

    public double getAvg() {
        return avg;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
