# Ecommerce Microservices Architecture

**A complete ecommerce demo
Implement based on [Microservice Architecture Pattern](http://martinfowler.com/microservices/) using Spring Boot, Spring Cloud, Spring WebFlux, Postgresdb and Docker.**


## Abstract
In this project I didn't built independent microservices But i have implement microservices share database Why?
Because I want to use database tools like `ACID transactions, joins, bulk files imports, ...`
instead of network overhead for interprocess communication between microservices.
I worked in a large project implemented based on microservices architecture we have a set of independent services and 
I used [Spring Cloud Open Feign](https://spring.io/projects/spring-cloud-openfeign) 
for interprocess communication (Which uses client side load balancer ribbon) 


I'm planning to implement another version from project with independent microservices and communicate via feign.

I have split services into two types system and logical services.

## Functional services

TODO (Documentation)


###  System Services
Core service to implement Netflix OSS design architecture and Services token security 

### Config Service
  Provides configuration for all other services (centralize configuration for all services).   <br>
Details: [Spring Cloud Config](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html)

### Discovery Service
It allows automatic detection of network locations for service instances, which could have dynamically assigned addresses because of auto-scaling, 
failures and upgrades (Every service register in this service after running).   <br>
Details: [Spring Cloud Config](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html)

### Authentication Service
Authorization responsibilities for all other services which grants [OAuth2 tokens](https://tools.ietf.org/html/rfc6749) for the backend resource services.
All other secured services must set jwk uri for endpoint implemented on this service.
```spring:
   security:
     oauth2:
       resourceserver:
         jwt:
           jwk-set-uri: ${JWKS_URL}
```
Endpoints

Method	| Path	| Description	| User authenticated	| Available from UI
------------- | ------------------------- | ------------- |:-------------:|:----------------:|
GET	| /.well-known/jwks	| Get JSON Web Key Set (JWKS) is a set of keys containing the public keys that should be used to verify JWT token	|  | 	
POST	| /user/merchant	| Register new merchant account (with merchant role)	| × | ×
POST	| /user/client	| Register new client account	(with client role)|   | 	×
GET	| /user	| Get current login user information | × | ×

### Gateway Service

Provide a proxy (routing) and client side load balancing via ribbon
You can deploy multiple services for the same service and gateway will load balancing between them ( Simple scalability )
Details: [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

### Monitoring

