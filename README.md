# Microservices Tutorial (Under Construction)

This is a tutorial project to show many approach used with microservices.

**Table of Contents**
* [About the tutorial](#about-the-tutorial)
* [Building, Testing and Running the springboot application](#building-testing-and-running-the-springboot-application)
* [Contacts](#contacts)

***

## About the tutorial

There are three different microservices in this system and they are chained together in the following sequence:

```
census → census-zipcode
census → census-demography
```

The idea is an api gateway call the census service and this one call the others to merge a couple of census information.

***

## Building, Testing and Running the springboot application

* building the microservices:
```
mvn clean install -f services/springboot/parent
mvn clean install -f services/springboot/internal-shared
mvn clean install -f services/springboot/internal-rest-http
mvn clean package -f services/springboot/census
mvn clean package -f services/springboot/census-zipcode
mvn clean package -f services/springboot/census-demography
```
* testing the microservices (from the jar, after having built it):
```
mvn clean verify -f services/springboot/census-zipcode
mvn clean verify -f services/springboot/census-demography
mvn clean verify -f services/springboot/census
```
* running the microservices (from the jar, after having built it):
```
java -jar services/springboot/census-zipcode/census-zipcode-infrastructure/target/census-zipcode.jar
java -jar services/springboot/census-demography/census-demography-infrastructure/target/census-demography.jar

// running without resilience4j
java -Dcensuszipcode.api.url=http://localhost:1401 -Dcensusdemography.api.url=http://localhost:1402 -jar services/springboot/census/census-infrastructure/target/census.jar

// running with resilience4j (circuit breaker and retry)
java -Dspring.profiles.active=resilience -Dcensuszipcode.api.url=http://localhost:1401 -Dcensusdemography.api.url=http://localhost:1402 -jar services/springboot/census/census-infrastructure/target/census.jar
```

Once the microservices are running, you can call:
```
curl http://localhost:1301/census/37188
```

