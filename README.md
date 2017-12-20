# my-retail-service

This project implements POC for `myRetail RESTful service` case study. The implementation uses the following technologies:
- `Spring Boot` for REST service
- `Elasticsearch` as a NoSql Document store for:
  - Product info in the `product` index
  - Product price info in the `product-price` index

## Version
| myRetail Service | Spring Boot    | Elasticsearch  | 
| -----------------| -------------- | -------------- | 
| SNAPSHOT         | 1.5.9.RELEASE  |   5.6.5        | 

## To build the project use the following command:

```
[PROJECT_HOME]/gradlew clean build
```

- This will create the JAR in the folder: `[PROJECT_HOME]/build/libs`
- Both unit and integration tests would be executed as part of build and report is available in the folder: `[PROJECT_HOME]/build/reports/tests`
- Code coverage report is available in the folder: `[PROJECT_HOME]/build/reports/cobertura`

## To run the tests use the following commands:

- To run unit tests use: `[PROJECT_HOME]/gradlew clean test`

- To run integration tests use: `[PROJECT_HOME]/gradlew clean iT`. Integration test use [EmbeddedElasticsearchServer](src/integrationtest/java/org/company/retail/EmbeddedElasticsearchServer.java) to validate repository functionality.

- To run both unit and integration tests use: `[PROJECT_HOME]/gradlew clean allTests`

## Steps to run the service locally:
1. [Download](https://www.elastic.co/downloads/past-releases/elasticsearch-5-6-5) and install Elasticsearch version 5.6.5.

2. Start Elasticsearch:
```sh
[ES_HOME]/bin/elasticsearch
```
3. Make sure you can access Elasticsearch at http://localhost:9200

```json
{
  "name" : "ekSpCGE",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "_7nJHeQsQmCgrN9zqsy-Kg",
  "version" : {
    "number" : "5.6.5",
    "build_hash" : "6a37571",
    "build_date" : "2017-12-04T07:50:10.466Z",
    "build_snapshot" : false,
    "lucene_version" : "6.6.1"
  },
  "tagline" : "You Know, for Search"
}
```

4. Create a couple of Product records:

```
POST http://localhost:9200/product/record/1000
{
  "id": 1000,
  "name": "Dish Washer"
}
```
```
POST http://localhost:9200/product/record/2000
{
  "id": 2000,
  "name": "Flat screen TV"
}

```
`Note:` Ideally we would create a mapping for the index and not use default mapping dervied from documents.

5. Create corresponding Pricing info for these products:

```
POST http://localhost:9200/product-price/record/1000
{
  "currency_code": "USD",
  "value": 679.50
}
```
```
POST http://localhost:9200/product-price/record/2000
{
  "currency_code": "EUR",
  "value": 800.34
}
```

6. Make sure you can access the records indexed in step 4 & 5
```
GET product/_search
GET product-price/_search
```

7. Startup the myRetail REST service from the project home folder:

```
[PROJECT_HOME]/gradlew clean bootRun
```

8. Get Product info for id `1000`

```
GET http://localhost:8080/products/1000
```
`Response:`
```
{
    "id": 1000,
    "name": "Dish Washer",
    "current_price": {
        "currency_code": "USD",
        "value": 679.5
    }    
}
```

9. Update pricing info for product with id `2000`

```
PUT http://localhost:8080/products/2000
{
    "id": 2000,
    "name": "Flat screen TV",
    "current_price": {
        "currency_code": "EUR",
        "value": 500.10
    }
   
}
```
`Response:`
```
{
    "id": 2000,
    "name": "Flat screen TV",
    "current_price": {
        "currency_code": "EUR",
        "value": 500.1
    }
}
```


## Following are important features that need to be implemented to make this production ready:

- [ ] [`Caching`](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html): Cache the Product info using `id` as key and invalidate it when updates to pricing are made.
- [ ] [`Hystrix`](https://cloud.spring.io/spring-cloud-netflix/multi/multi__circuit_breaker_hystrix_clients.html): If Elasticsearch cluster (data store) is struggling to recover, the microservice should use `circuit breaker` to stop sending requests to it for some time so that the cluster can recover completely.
- [ ] `Monitoring/Alering`: Service/request logs and metrics for the services/servers should be shiped to a central server for monitoring and alerting. `ELK` could be used to implement this.
- [ ] `API Management`: This includes security, API keys, rate limiting, etc. Ideally some API management tool like Apigee could be used for this.
- [ ] `Scalability`: To automate deployment/management of services to cluster of machines and to elastically scale the cluster based on load, we can use the following:
  - Containerization using `Docker` & `Kubernetes`
  - Service discovery/routing using `Spring Cloud`
