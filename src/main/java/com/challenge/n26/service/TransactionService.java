package com.challenge.n26.service;


import com.challenge.n26.controller.statistics.response.StatisticsResponse;
import com.challenge.n26.controller.transaction.request.TransactionRequest;

public interface TransactionService {

    public void addTransaction(TransactionRequest txn);

    public StatisticsResponse getStatistics();

    public void resetOldBucket();

}
