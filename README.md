# Microservices Tutorial (Under Construction)

This is an tutorial project to show many approach used with microservices.

**Table of Contents**
* [About the tutorial](#about-the-tutorial)
* [Building, Testing and Running the springboot application](#building-testing-and-running-the-springboot-application)
* [Contacts](#contacts)

***

## About the tutorial

There are three different microservices in this system and they are chained together in the following sequence:

```
census → zipcode
census → cityinformation
```

The idea is an api gateway call the census service and this one call the others to merge a couple of census information.

***

## Building, Testing and Running the springboot application

* building the microservices:
```
mvn clean install -f services/springboot/api-parent
mvn clean install -f services/springboot/internal-commons
mvn clean package -f services/springboot/api-census
mvn clean package -f services/springboot/api-zipcode
mvn clean package -f services/springboot/api-cityinformation
```
* testing the microservices (from the jar, after having built it):
```
mvn verify -f services/springboot/api-zipcode
mvn verify -f services/springboot/api-cityinformation
mvn verify -f services/springboot/api-census
```
* running the microservices (from the jar, after having built it):
```
java -jar services/springboot/api-zipcode/target/api-zipcode.jar
java -jar services/springboot/api-cityinformation/target/api-cityinformation.jar
java -Dzipcode.api.url=http://localhost:1401 -Dcityinformation.api.url=http://localhost:1402 -jar services/springboot/api-census/target/api-census.jar
```

Once the microservices are running, you can call:
```
curl http://localhost:1301/info/zip/37188
```

***

## Contacts
For any question or feedback (really appreciated!) feel free to contact me:
* Email: trevezani _(at)_ gmail.com
* Linkedin: [Alberto Trevezani](https://www.linkedin.com/in/albertotrevezani)
