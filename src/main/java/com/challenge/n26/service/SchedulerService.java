package com.challenge.n26.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler service which helps to purges older transactions from the Buffer
 * {@link com.challenge.n26.model.BufferTxnStatistics} and also updates
 * {@link com.challenge.n26.model.GlobalTxnStatistics}
 *
 */
@Component
public class SchedulerService {

    @Autowired
    private TransactionService transactionService;

    /**
     * scheduled method that runs every second to clean up old transactions.
     */
    @Scheduled(fixedRate = 1000)
    public void oldBucket() {
        transactionService.resetOldBucket(System.currentTimeMillis());
    }

}
