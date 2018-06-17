This is spring-boot maven based web application with has two endpoints.
1. **POST /transactions**
2. **GET /statistics**

Both endpoints have O(1) time complexity.

## RUN
1. mvn spring-boot:run

## How to Access
1. http://localhost:8080/swagger-ui.html#/

## Data-Structure
1.Array of constant size(CircularBuffer) is used to store all transaction related statistics. **POST /transactions** request are persisted in the circularBuffer.

2.GlobalStatistics Object is used to answer all **GET /statistics** request. 

3.Each index of the circularBuffer stores transaction statistics such as min, max, sum and count.
For every transaction, based on the timestamp in the given request, its index position in the circularBuffer is calculated and its corresponding statistics are updated and the same
transaction will also update globalStatistics.

## Integration-Test
ApplicationIntegrationTest does integration test of the two endpoints using **TestRestTemplate**

## Load-Test
Load test was done using gatling. Gatling script used for load-testing can be found in the path **/n26/src/main/resources/gatling** and zip file has the performance result.

  
 