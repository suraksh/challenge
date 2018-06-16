package com.challenge.n26.controller.transaction;

import com.challenge.n26.controller.transaction.request.TransactionRequest;
import com.challenge.n26.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller For POST transactions endpoint.
 * Validate and persist the transaction to in-memory.
 */
@RestController
@RequestMapping("/transactions")
@Api(value = "Transaction Controller")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    /**
     * Validates and persists transaction.
     * @param transactionRequest
     * @return 201 if transaction was successfully persisted.
     * 204 if transaction is older than 60 seconds.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Validate and persist Transaction", notes = "Add transaction to in-memory Data structure",
            code = 201)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully Persisted Transaction", response = ResponseEntity.class),
            @ApiResponse(code = 201, message = "Older Transaction"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<?> persistTransaction(@RequestBody TransactionRequest transactionRequest) {
        try {
            transactionService.addTransaction(transactionRequest);
            return new ResponseEntity<Object>(HttpStatus.CREATED);
        }catch(Exception ex) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }
    }
}
