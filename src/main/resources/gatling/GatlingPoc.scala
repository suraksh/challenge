package n26.challenge

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import java.util.concurrent._

class GatlingPoc extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:8080/")
		.inferHtmlResources()
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0")

	val headers_0 = Map(
		"Cache-Control" -> "no-cache",
		"Content-Type" -> "application/json")

	//val r = scala.util.Random

	val scn1969652 = scenario("N26 Challenge Gatling POST scenario").during(720 seconds){
			 exec(http("Get Statistics")
			.get("/statistics").check(status.is(200)))
			.exec{ session =>
              session
              		.set("amount", ThreadLocalRandom.current().nextDouble(-100000.00, 245000.00))
                    .set("timestamp", System.currentTimeMillis())}
			.exec(http("Post Transaction")
			.post("/transactions")
			.headers(headers_0)
			.body(ElFileBody("transactions.txt")).check(status.is(201)))
	}



	setUp(scn1969652.inject(rampUsers(5) over (5 seconds))).protocols(httpProtocol)

}