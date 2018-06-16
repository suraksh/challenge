package com.challenge.n26.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerService {

    @Autowired
    private TransactionService transactionService;

    @Scheduled(fixedRate = 1000)
    public void oldBucket() {
        transactionService.resetOldBucket();
    }

}
