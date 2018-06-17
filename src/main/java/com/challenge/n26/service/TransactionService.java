package com.challenge.n26.service;


import com.challenge.n26.controller.statistics.response.StatisticsResponse;
import com.challenge.n26.controller.transaction.request.TransactionRequest;
import com.challenge.n26.model.GlobalTxnStatistics;
import com.challenge.n26.service.exception.InvalidTxnException;

public interface TransactionService {


    /**
     * Validates if the TransactionRequest is not older than 60 seconds and add the
     * txn to the in-memory circularBuffer.
     * @param txn
     */
    public void addTransaction(TransactionRequest txn) throws InvalidTxnException;

    /**
     * Returns the statistic based on the transactions which happened in the last 60 seconds.
     * @return
     */
    public StatisticsResponse getStatistics();

    /**
     * Purges transactions statistics which are older than 60 seconds, Resets the bucket statistics
     * to 0 and also removes the corresponding bucket statistics from {@link GlobalTxnStatistics}
     * This method is called from scheduler every second.
     */
    public void resetOldBucket(long currentTimeStamp);

}
