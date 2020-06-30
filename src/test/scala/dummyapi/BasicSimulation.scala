package dummyapi

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://dummy.restapiexample.com/api/v1") // Here is the root for all relative URLs
    
  object Dummy{

    // feeder 
    val csvFeederCreate = csv("data/create-employee.csv").readRecords
    val csvFeederId = csv("data/id-employee.csv").readRecords

    // run test 
    val runTest = exec(http("dummy rest api - get all employee data")
      .get("/employees")
      .check(bodyString.saveAs("response_body")))
      .exec(session => {
        val response = session("response_body").as[String]
        println("\n\nResponse Body\n")
        println(response)
        println("\n\n")
        session
      })
      .pause(2) // pause kemudian hit untuk create user employee
      .foreach(csvFeederCreate, "feeder"){
        exec(flattenMapIntoAttributes("${feeder}"))
          .exec(http("dummy rest api - create employee")
          .post("/create")
          .header("Content-Type", "application/json")
          .header("Accept-Type", "application/json")
          .body(ElFileBody("bodies/create-body.json")).asJson
          .check(bodyString.saveAs("response_body")))
          .exec(session => {
            val response = session("response_body").as[String]
            println("\n\nResponse Body\n")
            println(response)
            println("\n\n")
            session
          })
      }
      .pause(2) // pause kemudian hit untuk get user employee based on user id
      .foreach(csvFeederId, "feeder"){
        exec(flattenMapIntoAttributes("${feeder}"))
          .exec(http("dummy rest api - get employee based on user id")
          .get("/employee/${id}")
          .header("Content-Type", "application/json")
          .header("Accept-Type", "application/json")
          .check(bodyString.saveAs("response_body")))
          .exec(session => {
            val response = session("response_body").as[String]
            println("\n\nResponse Body\n")
            println(response)
            println("\n\n")
            session
          })
      }

  }
  
  val scn = scenario("TEST DUMMY REST API EXAMPLE").exec(
    Dummy.runTest
  )

  setUp(
    scn.inject(
      atOnceUsers(1)
      ).protocols(httpProtocol))
}
