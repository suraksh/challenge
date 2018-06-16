package com.challenge.n26.model;

import com.challenge.n26.controller.statistics.response.StatisticsResponse;
import com.challenge.n26.controller.transaction.request.TransactionRequest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


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

    private boolean minOrMaxExpired(int evictionInMilliseconds) {
        long evictionTimeLimit = System.currentTimeMillis() - evictionInMilliseconds;
        return minTxn.getTimestamp() < evictionTimeLimit || maxTxn.getTimestamp() < evictionTimeLimit;
    }

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
