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
    private static final int GROUPING_FACTOR = 1000;
    private static final int EVICTION_IN_SECONDS = EVICTION_IN_MILLISECONDS / GROUPING_FACTOR;

    /**
     * Array of BufferTxnStatistics, whose size is equal to (eviction_time/grouping_factor).
     * This emulates circularBuffer , where each timestamp will map to an index position in the array.
     */
    private BufferTxnStatistics[] circularBuffer;

    /**
     * This class maintains global statistics, which is used by GET endpoint.
     */
    private GlobalTxnStatistics globalTxnStatistics;


    public TransactionServiceImpl() {
        this.circularBuffer = new BufferTxnStatistics[EVICTION_IN_MILLISECONDS / GROUPING_FACTOR];
        for (int i = 0; i < circularBuffer.length; i++) {
            circularBuffer[i] = new BufferTxnStatistics();
        }
        this.globalTxnStatistics = new GlobalTxnStatistics();
    }

    @Override
    public void addTransaction(TransactionRequest txn) throws InvalidTxnException{
        if (isBefore(txn)) throw new InvalidTxnException();
        int index = getIndexOfCircularBuffer(txn.getTimestamp());
        circularBuffer[index].addTxn(txn, index);
        globalTxnStatistics.updateGlobalTxnStatistics(txn);
        logger.info(String.format("Added new Txn amount  of %s to bucket of %s ", txn.getAmount(), index));
    }

    /**
     * Calculates to which bucket the timestamp belongs to.
     *
     * @param timestamp
     * @return Index position of the circularBuffer
     */
    private int getIndexOfCircularBuffer(long timestamp) {
        return (int) ((timestamp / GROUPING_FACTOR) % circularBuffer.length);
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


    /**
     * Validates whether txn timestamp is older than 60 seconds and if so returns true.
     * @param txn
     * @return true if txn is older than 60 seconds.
     */
    private boolean isBefore(TransactionRequest txn) {
        Instant txnInstant = Instant.ofEpochMilli(txn.getTimestamp());
        return txnInstant.isBefore(Instant.now().minusSeconds(60));
    }
}
