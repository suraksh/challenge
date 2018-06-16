package com.challenge.n26.controller.statistics;

import com.challenge.n26.controller.statistics.response.StatisticsResponse;
import com.challenge.n26.model.Statistics;
import com.challenge.n26.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for GET statistics endpoint.
 * Returns the statistic based on the transactions which happened in the last 60 seconds.
 */
@RestController
@RequestMapping("/statistics")
@Api(value = "Statistics Controller")
public class StatisticsController {


    /**
     * TransactionService.
     */
    @Autowired
    private TransactionService transactionService;


    /**
     * Returns the statistic based on the transactions which happened in the last 60 seconds
     * @return StatisticsResponse with 200 status.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns Statistics", notes = "Statistics of transactions of last 60 seconds ",
            code = 200)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Return Statistics", response = Statistics.class),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<StatisticsResponse> getStatistics(){
        return new ResponseEntity<StatisticsResponse>(transactionService.getStatistics(), HttpStatus.OK);
    }

}
