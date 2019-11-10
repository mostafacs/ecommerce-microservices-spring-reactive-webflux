# Ecommerce Microservices Architecture

**A complete ecommerce demo
Implement based on [Microservice Architecture Pattern](http://martinfowler.com/microservices/) using Spring Boot, Spring Cloud, Spring WebFlux, Postgresdb and Docker.**


## Abstract
In this project I didn't built independent microservices But i have implement microservices share database Why?
Because I want to use database tools like `ACID transactions, joins, bulk files imports, ...`
instead of network overhead for interprocess communication between microservices.
I worked in a large project implemented based on microservices architecture we have a set of independent services and 
I used [Spring Cloud Open Feign](https://spring.io/projects/spring-cloud-openfeign) 
for inter-process communication (Which uses client side load balancer ribbon) 


I'm planning to implement another version from project with independent microservices and communicate via feign.

I have split services into two types system and logical services.

##Features

Secured, Authorized and Paginated endpoints.

## Functional services

### Authentication Service
Authorization Server for all other services which grants [OAuth2 tokens](https://tools.ietf.org/html/rfc6749) for the backend resource services.
All other secured services must set jwk uri for endpoint implemented on this service.
```spring:
   security:
     oauth2:
       resourceserver:
         jwt:
           jwk-set-uri: ${JWKS_URL}
```
Endpoints

Method	| Path	| Description	| User authenticated	
------------- | ------------------------- | ------------- |:-------------:|
GET	| /.well-known/jwks	| System endpoint to get JSON Web Key Set (JWKS) is a set of keys containing the public keys that should be used to verify JWT token	|  
POST	| /user/merchant	| Register new merchant account (with merchant role)	|  
POST	| /user/client	| Register new client account	(with client role)|   | 
GET	| /user	| Get current login user information | × 

### Product Service
Manage products information and inventory
Endpoints

Method	| Path	| Description	| User authenticated	| Role
------------- | ------------------------- | ------------- |:-------------:| :-------------:|
GET	| /list?page={page}&pageSize={pageSize}	| Get all products	|  x | any |
GET	| /list/merchant?page={page}&pageSize={pageSize}		| Get products created by merchant	|  x | merchant,admin |
POST| /save	| Create new product| x  | merchant |
PUT	| /save	| Update existing product| x  | merchant |
GET	| /list/available?page={page}&pageSize={pageSize}		| Get available products with inventory > 0 | × | any
GET	| /list/not-available?page={page}&pageSize={pageSize}		| Get available products with inventory = 0 | × | any

### Order Service
Manage products information and inventory
Endpoints

Method	| Path	| Description	| User authenticated	| Role
------------- | ------------------------- | ------------- |:-------------:| :-------------:|
GET	| /list/user?page={page}&pageSize={pageSize}		| Get login user orders	|  x | any |
GET	| /{orderId}	| Get order by id	|  x | any |
POST| /save	| Create new order| x  | client |
PUT	| /save	| Update existing order| x  | client |


##  System Services
Core service to implement Netflix OSS design architecture and Services token security 

### Config Service
  Provides configuration for all other services (centralize configuration for all services).   <br>
Details: [Spring Cloud Config](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html)

### Discovery Service
It allows automatic detection of network locations for service instances, which could have dynamically assigned addresses because of auto-scaling, 
failures and upgrades (Every service register in this service after running).   <br>
Details: [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix) <br>
Eureka server url: http://localhost:2222/


### Gateway Service

Provide a proxy (routing) and client side load balancing via ribbon
You can deploy multiple services for the same service and gateway will load balancing between them ( Simple scalability )
Details: [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

### Migration Service
Migrations service handle changes on database tables using [flyway](https://flywaydb.org/) <br>
To apply change on database please create file in this folder `ecommerce-microservices/storage/migration/src/main/resources/flyway/migrations`
in this structure  `*__*.sql` then apply this command

```
cd ecommerce-microservices/storage/migration # change it to your path
mvn clean package
docker-compose build
docker-compose up migration
```

### Monitoring
Not completed require implement turbine service and any broker service (rabbitmq) But you can access 
start page from http://localhost:8000/hystrix

#Run Project
Install [maven](https://maven.apache.org/) and [docker](https://docs.docker.com/compose/)  <br>
``` 
mvn clean install 
docker-compse build
docker-compse up

# on the first run or after update sql migration on migration service
docker-compose up migration
```

Probably you got issues because docker caching refresh docker images by:
```
docker rm $(docker ps -a -q) -f
docker volume prune
```

# Endpoints Documentations
[Endpoints Docs](/endpoints.md)


## Contributions are welcome!
greatly appreciate your help. Feel free to suggest and implement improvements.


