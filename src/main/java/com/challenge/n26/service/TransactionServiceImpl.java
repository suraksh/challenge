package com.challenge.n26.service;

import com.challenge.n26.controller.statistics.response.StatisticsResponse;
import com.challenge.n26.controller.transaction.request.TransactionRequest;
import com.challenge.n26.model.BufferTxnStatistics;
import com.challenge.n26.model.GlobalTxnStatistics;
import com.challenge.n26.service.exception.InvalidTxnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LogManager.getLogger(TransactionServiceImpl.class);
    private static final int EVICTION_IN_MILLISECONDS = 60000;
    private static final int THOUSAND = 1000;
    private static final int EVICTION_IN_SECONDS = EVICTION_IN_MILLISECONDS/THOUSAND;
    private BufferTxnStatistics[] circularBuffer;
    private GlobalTxnStatistics globalTxnStatistics;


    public TransactionServiceImpl() {
        this.circularBuffer = new BufferTxnStatistics[EVICTION_IN_MILLISECONDS/THOUSAND];
        for(int i = 0; i < circularBuffer.length; i++) {
            circularBuffer[i] = new BufferTxnStatistics();
        }
        this.globalTxnStatistics = new GlobalTxnStatistics();
    }

    @Override
    public void addTransaction(TransactionRequest txn) {
        if(isBefore(txn)) throw new InvalidTxnException();
        int index = getIndexOfCircularBuffer(txn.getTimestamp());
        circularBuffer[index].addTxn(txn, index);
        globalTxnStatistics.updateGlobalTxnStatistics(txn);
        logger.info(String.format("Added new Txn amount  of %s to bucket of %s ", txn.getAmount(), index));
    }

    private int getIndexOfCircularBuffer(long timestamp) {
        return (int)((timestamp/THOUSAND) % circularBuffer.length);
    }

    @Override
    public StatisticsResponse getStatistics() {
        return globalTxnStatistics.getStatistics(circularBuffer, EVICTION_IN_MILLISECONDS);
    }

    @Override
    public void resetOldBucket() {
        long currentTime = System.currentTimeMillis();
        int index = getIndexOfCircularBuffer(currentTime);
        circularBuffer[index].cleanUpTxn(globalTxnStatistics);
        logger.info(String.format("Clearing bucket of %s due to scheduler call ", index));
    }

    private boolean isBefore(TransactionRequest txn) {
        Instant txnInstant = Instant.ofEpochMilli(txn.getTimestamp());
        return txnInstant.isBefore(Instant.now().minusSeconds(60));
    }
}
