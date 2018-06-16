package com.challenge.n26.model;

import com.challenge.n26.controller.transaction.request.TransactionRequest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 *  The class holds statistics of min, max, sum and count.
 * This is the core class and data structure used by {@link com.challenge.n26.service.TransactionService}
 * The class {@link com.challenge.n26.service.TransactionService} creates an array of the class
 * {@code BufferTxnStatistics} and treats this array as circularBuffer.
 * For each transaction request, {@link com.challenge.n26.service.TransactionService} calculates the index of the array
 * {@code BufferTxnStatistics} and updates the statistics in that corresponding index.
 * This class uses {@link ReentrantLock} to handle concurrent requests.
 */
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

    /**
     * Adds the transaction amount to current index statistics.
     * @param txn
     * @param index
     */
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

    /*
    Helper method which returns Statistics from the current buffer statistics.
     */
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

    /**
     * Reset statistics of the current index of {@code {@link BufferTxnStatistics}} and also
     * updates the statistics of {@link GlobalTxnStatistics}
     * @param globalTxnStatistics
     */
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
