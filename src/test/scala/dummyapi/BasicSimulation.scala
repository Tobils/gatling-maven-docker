package dummyapi

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


// parse json  library
import scala.util.parsing.json._

// library java untuk write file ke dalam bentuk .csv 
import java.io.File
import java.io.PrintWriter
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}


// library untuk save response ke dalam .csv
import java.io.{BufferedWriter, FileWriter}
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.Random

import com.github.tototoshi.csv._

class CC[T] { def unapply(a:Any):Option[T] = Some(a.asInstanceOf[T])}
object M extends CC[Map[String, Any]]
object L extends CC[List[Any]]
object S extends CC[String]
object D extends CC[Double]
object B extends CC[Boolean] 

class BasicSimulation extends Simulation {
  val f = new File("src/test/resources/data/dummy-api-response.csv")
  val writer = CSVWriter.open(f)

  val httpProtocol = http
    .baseUrl("http://dummy.restapiexample.com/api/v1") // Here is the root for all relative URLs

  object DateUtil {
    private val dateFmt = "yyyy-MM-dd HH::mm::ss"

    def today(): String = {
      val date = new Date
      val sdf = new SimpleDateFormat(dateFmt)
      sdf.format(date)
    }
  }
    
  object Dummy{

    def simpanResponse(response: String) = {
      println("\n\nResponse Body\n")
      println(response)
      println("\n\n")

      val data = response.stripMargin

      val Result = scala.collection.mutable.Map[String, String]()

      // 1. extract json 
      for {
        Some(M(map)) <- List(JSON.parseFull(data))
        S(status) = map("status")
        M(datas) = map("data")
        S(name) = datas("employee_name") 
        S(salary) = datas("employee_salary")
        S(age) = datas("employee_age") 


      } yield {
        println("\n\nResponse Extracted\n")
        Result += ("datetime" -> DateUtil.today())
        Result += ("name" -> name)
        Result += ("age" -> age)
        Result += ("salary" -> salary)

        println(Result)

        
        writer.writeRow(List(DateUtil.today(), name, age, salary))
        // writer.close()
      }
    }

    // feeder 
    val csvFeederCreate = csv("data/create-employee.csv").readRecords
    val csvFeederId = csv("data/id-employee.csv").readRecords

    // run test 
    val runTest = exec(http("dummy rest api - get all employee data")
      .get("/employees")
      .check(bodyString.saveAs("response_body")))
      .exec(session => {
        val response = session("response_body").as[String]
        println(response)
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
            println(response)
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

            // simpan response
            simpanResponse(response)
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
