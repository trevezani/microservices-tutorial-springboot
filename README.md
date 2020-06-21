# Microservices Tutorial (Under Construction)

This is a tutorial project to show many approach used with microservices.

**Table of Contents**
* [About the tutorial](#about-the-tutorial)
* [Building, Testing and Running the springboot application](#building-testing-and-running-the-springboot-application)
* [Building, Testing and Running the springboot application with Consul](#building-testing-and-running-the-springboot-application-with-consul)
* [Building, Testing and Running the springboot application with Consul (docker mode)](#building-testing-and-running-the-springboot-application-with-consul-(docker-mode)]

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

or execute the bash `buildDefault.sh` inside the directory `services/springboot`

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

***

## Building, Testing and Running the springboot application with Consul

* running the consul for dev

```
docker run -d --name consul-1 -p 8500:8500 -e CONSUL_BIND_INTERFACE=eth0 consul

// the join address is the adress for the first agent
docker exec -it consul-1 consul members

docker run -d --name consul-2 -e CONSUL_BIND_INTERFACE=eth0 -p 8501:8500 consul agent -dev -join=172.17.0.2
docker run -d --name consul-3 -e CONSUL_BIND_INTERFACE=eth0 -p 8502:8500 consul agent -dev -join=172.17.0.2
```
* running the consul (another option)
```
docker run -d --name consul-server-1 -p 8500:8500 consul:1.7.3 agent -server -bootstrap-expect 3 -ui -client 0.0.0.0 -bind 0.0.0.0

// the join address is the adress for the first server
docker exec -it consul-1 consul members

docker run -d --name consul-server-2 consul:1.7.3 agent -server -retry-join 172.17.0.4 -client 0.0.0.0 -bind 0.0.0.0
docker run -d --name consul-server-3 consul:1.7.3 agent -server -retry-join 172.17.0.4 -client 0.0.0.0 -bind 0.0.0.0

docker run -d --name consul-client-1 consul:1.7.3 agent -retry-join 172.17.0.4 -client 0.0.0.0 -bind 0.0.0.0
docker run -d --name consul-client-2 consul:1.7.3 agent -retry-join 172.17.0.4 -client 0.0.0.0 -bind 0.0.0.0
```

Link: [http://localhost:8500/ui](http://localhost:8500/ui)

Inside the link, create the key/value below:

```
config/census/censusdemography.api.url = http://census-demography
config/census/censuszipcode.api.url = http://census-zipcode
config/census-zipcode/server.port = 0
config/census-demography/server.port = 0
```

* building the microservices:
```
mvn clean install -f services/springboot/parent
mvn clean install -f services/springboot/internal-shared
mvn clean install -f services/springboot/internal-rest-http
mvn clean package -f services/springboot/census-gateway
mvn clean package -Pconsul -f services/springboot/census
mvn clean package -Pconsul -f services/springboot/census-zipcode
mvn clean package -Pconsul -f services/springboot/census-demography
```

or execute the bash `buildConsul.sh` inside the directory `services/springboot`

* testing the microservices (from the jar, after having built it):
```
mvn clean verify -f services/springboot/census-zipcode
mvn clean verify -f services/springboot/census-demography
mvn clean verify -f services/springboot/census
```
* running the microservices (from the jar, after having built it):
```
java -Dspring.cloud.consul.host=[local IP] -jar census-gateway/target/census-gateway.jar
java -Dspring.profiles.active=consul -Dspring.cloud.consul.host=[local IP] -jar census/census-infrastructure/target/census.jar
java -Dspring.profiles.active=consul -Dspring.cloud.consul.host=[local IP] -jar census-zipcode/census-zipcode-infrastructure/target/census-zipcode.jar
java -Dspring.profiles.active=consul -Dspring.cloud.consul.host=[local IP] -jar census-demography/census-demography-infrastructure/target/census-demography.jar
```

Once the microservices are running, you can call:
```
curl http://localhost:1301/census/37188

curl http://localhost:1200/census/37188
curl http://localhost:1200/zipcode/37188
curl http://localhost:1200/demography/37188
```

***

## Building, Testing and Running the springboot application with Consul (docker mode)

* building the microservices:
```
mvn clean install -f services/springboot/parent
mvn clean install -f services/springboot/internal-shared
mvn clean install -f services/springboot/internal-rest-http
mvn clean package -Pconsul -f services/springboot/census
mvn clean package -Pconsul -f services/springboot/census-zipcode
mvn clean package -Pconsul -f services/springboot/census-demography

mvn docker:build -f services/springboot/census/census-infrastructure
mvn docker:build -f services/springboot/census-zipcode/census-zipcode-infrastructure
mvn docker:build -f services/springboot/census-demography/census-demography-infrastructure
```

or execute the bash `buildDocker.sh` inside the directory `services/springboot`

* running the consul:
```
docker network create -d bridge consul-net

docker-compose -f compose/docker-compose-springboot-consul.yml up
```

Link: [http://localhost:8500/ui](http://localhost:8500/ui)

Inside the link, create the key/value below:

```
config/census/censusdemography.api.url = http://census-demography
config/census/censuszipcode.api.url = http://census-zipcode
```
* running the microservices:
```
docker-compose -f compose/docker-compose-springboot-census.yml up
```

Once the microservices are running, you can call:
```
curl http://localhost:1311/census/37188
```

* running the gateway:
```
docker-compose -f compose/docker-compose-springboot-gateway.yml up
```

Once the gateway is running, you can call:
```
curl http://localhost:1201/census/37188
curl http://localhost:1201/zipcode/37188
curl http://localhost:1201/demography/37188
```

* running the monitor:
```
docker-compose -f compose/docker-compose-springboot-monitor.yml up
```

Links: [[Prometheus]](http://localhost:9090/) [[Grafana]](http://localhost:3000/)

Grafana Dashbords:

[https://grafana.com/grafana/dashboards/10642](https://grafana.com/grafana/dashboards/10642)

[https://grafana.com/grafana/dashboards/4701](https://grafana.com/grafana/dashboards/4701)

* running the logging:
```
docker-compose -f compose/docker-compose-springboot-logging.yml up
```

Link: [http://localhost:5601/](http://localhost:5601/)

* checking the memory
```
docker stats $(docker ps --format={{.Names}})
```
