package com.challenge.n26.model;

import com.challenge.n26.controller.statistics.response.StatisticsResponse;
import com.challenge.n26.controller.transaction.request.TransactionRequest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * The class maintains global statistics such as min, max, count, sum which is used by
 * {@link com.challenge.n26.controller.statistics.StatisticsController} to return statistics.
 * The class {@link com.challenge.n26.service.TransactionService} also updates the global statistics
 * for each valid transaction by holding ReentrantLock.
 * Scheduler removes statistics count from {@link BufferTxnStatistics} and removes the corresponding
 * count and sum of that buffer index from {@code GlobalTxnStatistics}.
 * All update operations uses lock to handle concurrent modification.
 */
public class GlobalTxnStatistics {

    private Transaction minTxn;
    private Transaction maxTxn;
    private long count;
    private double sum;
    private final Lock lock = new ReentrantLock();

    public GlobalTxnStatistics() {
        minTxn = new Transaction(Double.MAX_VALUE, 0L);
        maxTxn = new Transaction(Double.MIN_VALUE, 0L);
    }

    /**
     * Called by {@link com.challenge.n26.service.TransactionServiceImpl} to update
     * global statistics.
     * @param txn
     */
    public void updateGlobalTxnStatistics(TransactionRequest txn) {
        lock.lock();
        try{
            count += 1;
            if(txn.getAmount() < minTxn.getAmt()) {
                minTxn.setAmt(txn.getAmount());
                minTxn.setTimestamp(txn.getTimestamp());
            }
            if(txn.getAmount() > maxTxn.getAmt()) {
                maxTxn.setAmt(txn.getAmount());
                maxTxn.setTimestamp(txn.getTimestamp());
            }
            sum += txn.getAmount();
        }finally {
            lock.unlock();
        }
    }

    /**
     * Called by GET statistics endpoint, to return current global statistics of all transactions within
     * last 60 seconds.
     *
     * @param circularBuffer
     * @param evictionInMilliseconds
     * @return
     */
    public StatisticsResponse getStatistics(BufferTxnStatistics[] circularBuffer, int evictionInMilliseconds) {
        if(this.count == 0L) return new StatisticsResponse();
        if(minOrMaxExpired(evictionInMilliseconds)) {
            lock.lock();
            minTxn = new Transaction(Double.MAX_VALUE, 0L);
            maxTxn = new Transaction(Double.MIN_VALUE, 0L);
            try{
                for(int i = 0; i < circularBuffer.length; i++) {
                    Statistics currState = circularBuffer[i].convertCurrStatDetailsToStatistics();
                    if(currState.getMin() < minTxn.getAmt()) {
                        minTxn.setAmt(currState.getMin());
                        minTxn.setTimestamp(currState.getTimestamp());
                    }
                    if(currState.getMax() > maxTxn.getAmt()) {
                        maxTxn.setAmt(currState.getMin());
                        maxTxn.setTimestamp(currState.getTimestamp());
                    }
                }
            }finally {
                lock.unlock();
            }
        }
        StatisticsResponse st = new StatisticsResponse();
        lock.lock();
        try{
            st.setCount(this.count);
            st.setAvg(this.sum/this.count);
            st.setSum(this.sum);
            st.setMax(this.maxTxn.getAmt());
            st.setMin(this.minTxn.getAmt());
        }finally {
            lock.unlock();
        }
        return st;
    }

    /*
    Helper method to identify if the global min or max holds expired transaction min or max amount.
     */
    private boolean minOrMaxExpired(int evictionInMilliseconds) {
        long evictionTimeLimit = System.currentTimeMillis() - evictionInMilliseconds;
        return minTxn.getTimestamp() < evictionTimeLimit || maxTxn.getTimestamp() < evictionTimeLimit;
    }

    /**
     * Scheduler calls this method to remove old bucket statistics from global statistics count.
     * @param count
     * @param sum
     */
    public void removeBucketStatistics(long count, double sum) {
        lock.lock();
        try{
            this.count -= count;
            this.sum -= sum;
        }finally {
            lock.unlock();
        }
    }
}
