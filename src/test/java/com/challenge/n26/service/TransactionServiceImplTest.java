package com.challenge.n26.service;

import com.challenge.n26.controller.statistics.response.StatisticsResponse;
import com.challenge.n26.controller.transaction.request.TransactionRequest;
import com.challenge.n26.service.exception.InvalidTxnException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit test for {@link TransactionServiceImpl}.
 */
public class TransactionServiceImplTest {

    private static final double AMOUNT = 12.3;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private TransactionService transactionService;

    @Before
    public void setup() {
        transactionService = new TransactionServiceImpl();
    }

    /**
     * Test {@link TransactionServiceImpl#addTransaction(TransactionRequest)} should throw InvalidTxnException.
     * Add a transaction whose timestamp is older than 60 seconds.
     * @throws Exception
     */
    @Test
    public void testAddTransactionWhenTimestampIsOlderShouldThrowInvalidTxnException() throws Exception {
        thrown.expect(InvalidTxnException.class);
        TransactionRequest txn = new TransactionRequest();
        txn.setAmount(AMOUNT);
        txn.setTimestamp(System.currentTimeMillis()-100000);
        transactionService.addTransaction(txn);
    }

    /**
     * Test {@link TransactionServiceImpl#addTransaction(TransactionRequest)} should succeed.
     * Add a transaction whose timestamp is within 60 seconds compared to Current Time.
     * @throws Exception
     */
    @Test
    public void testAddTransactionWithValidTxnReqestShouldPersist() throws Exception{
        TransactionRequest txn = new TransactionRequest();
        txn.setAmount(AMOUNT);
        txn.setTimestamp(System.currentTimeMillis());
        transactionService.addTransaction(txn);
    }

    /**
     * Test {@link TransactionServiceImpl#getStatistics()} should return 0 for max, min, count, sum, avg.
     * Call {@link TransactionServiceImpl#getStatistics()} on {@link TransactionServiceImpl} without adding
     * any transaction to circularBuffer.
     */
    @Test
    public void testGetStatisticsShouldReturnDefaultValues() {
        StatisticsResponse response = transactionService.getStatistics();
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getAvg(), 0.0, 0);
        Assert.assertEquals(response.getCount(), 0L);
        Assert.assertEquals(response.getMax(), 0.0, 0);
        Assert.assertEquals(response.getMin(), 0.0, 0);
        Assert.assertEquals(response.getSum(), 0.0, 0);
    }

    /**
     * Test {@link TransactionServiceImpl#getStatistics()} with just one {@link TransactionRequest} added
     * to buffer and validate statistics result.
     * @throws Exception
     */
    @Test
    public void testGetStatisticsOfOneTxnShouldReturnValidStatistics() throws Exception{
        TransactionRequest txn = new TransactionRequest();
        txn.setAmount(AMOUNT);
        txn.setTimestamp(System.currentTimeMillis());
        transactionService.addTransaction(txn);
        StatisticsResponse response = transactionService.getStatistics();
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getAvg(), AMOUNT, 0);
        Assert.assertEquals(response.getCount(), 1L);
        Assert.assertEquals(response.getMax(), AMOUNT, 0);
        Assert.assertEquals(response.getMin(), AMOUNT, 0);
        Assert.assertEquals(response.getSum(), AMOUNT, 0);
    }

    /**
     * Test {@link TransactionServiceImpl#getStatistics()} with 10 {@link TransactionRequest} added
     * to buffer and validate statistics result.
     * @throws Exception
     */
    @Test
    public void testGetStatisticsOfMoreThanOneTxnShouldReturnValidStatistics() throws Exception{
        TransactionRequest txn = new TransactionRequest();
        int total_loops = 10;
        txn.setAmount(AMOUNT);
        txn.setTimestamp(System.currentTimeMillis());
        for(int i = 0; i < total_loops; i++) {
            transactionService.addTransaction(txn);
        }
        StatisticsResponse response = transactionService.getStatistics();
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getAvg(), AMOUNT, 0.001);
        Assert.assertEquals(response.getCount(), total_loops);
        Assert.assertEquals(response.getMax(), AMOUNT, 0);
        Assert.assertEquals(response.getMin(), AMOUNT, 0);
        Assert.assertEquals(response.getSum(), 123.0, 0.001);
    }

    /**
     * Test {@link TransactionServiceImpl#getStatistics()} with 12 {@link TransactionRequest} added
     * to buffer and validate statistics result.
     * @throws Exception
     */
    @Test
    public void testGetStatisticsOfMoreThanOneTxnWithDifferentMinMaxShouldReturnValidStatistics()
            throws Exception{
        double max = 115.0;
        double min = 5.0;
        TransactionRequest txn = new TransactionRequest();
        int total_loops = 12;
        txn.setAmount(AMOUNT);
        txn.setTimestamp(System.currentTimeMillis());
        for(int i = 0; i < total_loops-2; i++) {
            transactionService.addTransaction(txn);
        }
        txn.setAmount(min);
        transactionService.addTransaction(txn);
        txn.setAmount(max);
        transactionService.addTransaction(txn);
        double sum = 243.0;
        double avg = sum/total_loops;
        StatisticsResponse response = transactionService.getStatistics();
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getAvg(), avg, 0.001);
        Assert.assertEquals(response.getCount(), total_loops);
        Assert.assertEquals(response.getMax(), max, 0);
        Assert.assertEquals(response.getMin(), min, 0);
        Assert.assertEquals(response.getSum(), sum, 0.001);
    }

}