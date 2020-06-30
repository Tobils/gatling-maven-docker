# GATLING + MAVEN + DOCKER

Menjalankan gatling untuk loading test mengguanankan plugin maven yang dijalankan di atas docker. kita kan menggunakan dummy rest api example sebagai target load test kita. public api yang akan kita test ditampilkan pada table dibawah ini.

| No   | Route   | Method   | Type   | Full Route   | Description| 
|---|---|---|---|---|---|
| 1   | /employee  | GET  | JSON   | http://dummy.restapiexample.com/api/v1/employees   | Get all employee data   |
| 2  | /employee/{id}  | GET  | JSON   | http://dummy.restapiexample.com/api/v1/employee/1  | Get a single employee data  |
| 3  | /create  | POST   | JSON  | http://dummy.restapiexample.com/api/v1/create  | Create new record in database  |
| 4  | /update/{id}  | PUT  | JSON  | http://dummy.restapiexample.com/api/v1/update/21  | Update an employee record  |
| 5  | /delete/{id}  | DELETE  | JSON  | http://dummy.restapiexample.com/api/v1/delete/2  | Delete an employee record  |

## 1. GATLING-HTTP2
- Atur http2 enable pada ptotokol http, sebagaimana contoh berikut :
    ```scala
    val httpProtocol = http.baseUrl("host-name").enableHttp2
    ```

## 2. GATLING-FEEDER-BODY
- Data disimpan pada path resources/data, sebagai contoh pada file search.csv dengan data berikut :
    ```csv
    searchCriterion,searchComputerName
    Macbook,MacBook Pro
    eee,ASUS Eee PC 1005PE
    ```
- Data Body request disimpan pada path resources/bodies

## 3. GATLING-SAVE-RESPONSE

## 4. GATLING-CHECK


## REFERENSI
- [gatling - official doc](https://gatling.io)
- [gatling - maven plugin demo - github](https://github.com/gatling/gatling-maven-plugin-demo)
- [gatling - advanced tutorial](https://gatling.io/docs/current/advanced_tutorial/)
- [dummy rest-api](http://dummy.restapiexample.com/)




