# my-retail-service

This project implements POC for `myRetail RESTful service` case study. The implementation uses the following technologies:
- `Spring Boot` for REST service
- `Elasticsearch` as a NoSql Document store to store:
  - Product info in the `product` index
  - Product price info in the `product-price` index

## Versions
| Spring Boot    | Elasticsearch  | 
| -------------- | -------------- | 
| 1.5.9.RELEASE  | 5.6.5          | 

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

## TODO

- [ ] Write missing unit tests.
- [ ] Write comments in source code.
- [ ] Write integration tests for the repository layer.
