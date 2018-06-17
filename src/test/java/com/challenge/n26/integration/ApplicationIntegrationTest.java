package com.challenge.n26.integration;

import com.challenge.n26.controller.transaction.request.TransactionRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationIntegrationTest {

    private static final double AMOUNT = 12.3;
    private static final int OLD_TXN = 61000;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testPersistTransactionReturns201() {
        TransactionRequest req = new TransactionRequest();
        req.setAmount(AMOUNT);
        req.setTimestamp(System.currentTimeMillis());
        ResponseEntity<?> response = this.restTemplate.postForEntity("/transactions",
                req, Object.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void testPersistTransactionReturns204() {
        TransactionRequest req = new TransactionRequest();
        req.setAmount(AMOUNT);
        req.setTimestamp(System.currentTimeMillis()-OLD_TXN);
        ResponseEntity<?> response = this.restTemplate.postForEntity("/transactions",
                req, Object.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }
}
