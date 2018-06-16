package com.challenge.n26.model;

import com.challenge.n26.controller.transaction.request.TransactionRequest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferTxnStatistics {

    private double min;
    private double max;
    private double sum;
    private long count;
    private final Lock lock = new ReentrantLock();

    public BufferTxnStatistics() {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
    }

    public void addTxn(TransactionRequest txn, int index) {
        lock.lock();
        try {
            sum += txn.getAmount();
            count += 1;
            min = Math.min(min, txn.getAmount());
            max = Math.max(max, txn.getAmount());
        }finally{
            lock.unlock();
        }
    }

    public Statistics convertCurrStatDetailsToStatistics() {
        lock.lock();
        Statistics st = new Statistics();
        try {
            st.setCount(this.count);
            st.setMax(this.max);
            st.setMin(this.min);
            st.setSum(this.sum);
            st.setAvg(this.sum/this.count);
        }finally {
            lock.unlock();
        }
        return st;
    }

    public void cleanUpTxn(GlobalTxnStatistics globalTxnStatistics) {
        lock.lock();
        try {
            globalTxnStatistics.removeBucketStatistics(this.count, this.sum);
            this.count = 0;
            this.sum = 0;
            this.max = Double.MIN_VALUE;
            this.min = Double.MAX_VALUE;
        }finally {
            lock.unlock();
        }
    }
}
