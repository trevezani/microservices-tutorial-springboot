# Microservices Tutorial Springboot (Under Construction)

This is a tutorial project to show many approach used with microservices.

**Table of Contents**
* [About the tutorial](#about-the-tutorial)
* [Building, Testing and Running the springboot application](#building-testing-and-running-the-springboot-application)
* [Building, Testing and Running the springboot application with Consul](#building-testing-and-running-the-springboot-application-with-consul)
* [Building, Testing and Running the springboot application with Consul (docker mode)](#building-testing-and-running-the-springboot-application-with-consul-docker-mode)

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
docker network create --driver=bridge --subnet=192.168.0.0/16 census-net

// Get a keygen
docker run -it --rm --name consul_command \
       --network=census-net \
       consul:1.8.0 consul keygen

// Change the encrypt information using the  generated keygen above
docker run -d --name consul-server-1 \
       --network=census-net \
       --ip 192.168.255.241 \
       -p 18300:8300 \
       -p 18301:8301 \
       -p 18301:8301/udp \
       -p 18302:8302 \
       -p 18302:8302/udp \
       -p 18400:8400 \
       -p 18500:8500 \
       -p 18600:8600 \
       -p 18600:8600/udp \
       -e 'CONSUL_LOCAL_CONFIG={"bootstrap_expect": 3, "client_addr": "0.0.0.0", "datacenter": "Us-Central", "data_dir": "/var/consul", "domain": "consul", "enable_script_checks": true, "dns_config": {"enable_truncate": true, "only_passing": true}, "encrypt": "6uZf92Qa7dFgGnQ1zh8Hn0MwnRh1bRAOlE481Mv4+cU=", "leave_on_terminate": true, "log_level": "INFO", "rejoin_after_leave": true, "server": true, "telemetry": {"prometheus_retention_time": "24h", "disable_hostname": true}, "start_join": ["192.168.255.241","192.168.255.242","192.168.255.243"], "ui": true}' \
       consul:1.8.0 agent

docker run -d --name consul-server-2 \
       --network=census-net \
       --ip 192.168.255.242 \
       -p 28300:8300 \
       -p 28301:8301 \
       -p 28301:8301/udp \
       -p 28302:8302 \
       -p 28302:8302/udp \
       -p 28400:8400 \
       -p 28500:8500 \
       -p 28600:8600 \
       -p 28600:8600/udp \
       -e 'CONSUL_LOCAL_CONFIG={"bootstrap_expect": 3, "client_addr": "0.0.0.0", "datacenter": "Us-Central", "data_dir": "/var/consul", "domain": "consul", "enable_script_checks": true, "dns_config": {"enable_truncate": true, "only_passing": true}, "encrypt": "6uZf92Qa7dFgGnQ1zh8Hn0MwnRh1bRAOlE481Mv4+cU=", "leave_on_terminate": true, "log_level": "INFO", "rejoin_after_leave": true, "server": true, "telemetry": {"prometheus_retention_time": "24h", "disable_hostname": true}, "start_join": ["192.168.255.241","192.168.255.242","192.168.255.243"], "ui": true}' \
       consul:1.8.0 agent

docker run -d --name consul-server-3 \
       --network=census-net \
       --ip 192.168.255.243 \
       -p 38300:8300 \
       -p 38301:8301 \
       -p 38301:8301/udp \
       -p 38302:8302 \
       -p 38302:8302/udp \
       -p 38400:8400 \
       -p 38500:8500 \
       -p 38600:8600 \
       -p 38600:8600/udp \
       -e 'CONSUL_LOCAL_CONFIG={"bootstrap_expect": 3, "client_addr": "0.0.0.0", "datacenter": "Us-Central", "data_dir": "/var/consul", "domain": "consul", "enable_script_checks": true, "dns_config": {"enable_truncate": true, "only_passing": true}, "encrypt": "6uZf92Qa7dFgGnQ1zh8Hn0MwnRh1bRAOlE481Mv4+cU=", "leave_on_terminate": true, "log_level": "INFO", "rejoin_after_leave": true, "server": true, "telemetry": {"prometheus_retention_time": "24h", "disable_hostname": true}, "start_join": ["192.168.255.241","192.168.255.242","192.168.255.243"], "ui": true}' \
       consul:1.8.0 agent

docker run -d --name consul-agent-1 \
       --network=census-net \
       --ip 192.168.255.244 \
       -e 'CONSUL_LOCAL_CONFIG={"server": false, "datacenter": "Us-Central", "data_dir": "/var/consul", "encrypt": "6uZf92Qa7dFgGnQ1zh8Hn0MwnRh1bRAOlE481Mv4+cU=","log_level": "INFO", "leave_on_terminate": true, "start_join": ["192.168.255.242"]}' \
       consul:1.8.0 agent

docker run -d --name consul-agent-2 \
       --network=census-net \
       --ip 192.168.255.245 \
       -e 'CONSUL_LOCAL_CONFIG={"server": false, "datacenter": "Us-Central", "data_dir": "/var/consul", "encrypt": "6uZf92Qa7dFgGnQ1zh8Hn0MwnRh1bRAOlE481Mv4+cU=","log_level": "INFO", "leave_on_terminate": true, "start_join": ["192.168.255.243"]}' \
       consul:1.8.0 agent
```

Below the json used in the consul setup:

```
//Server
{
    "bootstrap_expect": 3,
    "client_addr": "0.0.0.0",
    "datacenter": "Us-Central",
    "data_dir": "/var/consul",
    "domain": "consul",
    "enable_script_checks": true,
    "dns_config": {
        "enable_truncate": true,
        "only_passing": true
    },
    "encrypt": "6uZf92Qa7dFgGnQ1zh8Hn0MwnRh1bRAOlE481Mv4+cU=",
    "leave_on_terminate": true,
    "log_level": "INFO",
    "rejoin_after_leave": true,
    "server": true,
    "telemetry": {
        "prometheus_retention_time": "24h",
        "disable_hostname": true
    },                
    "start_join": [
        "192.168.255.241",
        "192.168.255.242",
        "192.168.255.243"
    ],
    "ui": true
}

// Agent
{
    "server": false,
    "datacenter": "Us-Central",
    "data_dir": "/var/consul",
    "encrypt": "6uZf92Qa7dFgGnQ1zh8Hn0MwnRh1bRAOlE481Mv4+cU=",
    "log_level": "INFO",
    "leave_on_terminate": true,
    "start_join": [
        "192.168.255.242"
    ]
}
```

If you want see the consul cluster use this command:

```
docker exec -it consul-server-1 consul members
```

To remove a service from the consul use this command:

```
docker exec -it consul-server-1 consul services deregister -id=<service id> // example census-zipcode-734475
```

Link: [http://localhost:8500/ui](http://localhost:18500/ui)

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
java -Dspring.cloud.consul.host=[local IP] -Dspring.cloud.consul.port=18500 -jar census-gateway/target/census-gateway.jar
java -Dspring.profiles.active=consul -Dspring.cloud.consul.host=[local IP] -Dspring.cloud.consul.port=18500 -jar census/census-infrastructure/target/census.jar
java -Dspring.profiles.active=consul -Dspring.cloud.consul.host=[local IP] -Dspring.cloud.consul.port=18500 -jar census-zipcode/census-zipcode-infrastructure/target/census-zipcode.jar
java -Dspring.profiles.active=consul -Dspring.cloud.consul.host=[local IP] -Dspring.cloud.consul.port=18500 -jar census-demography/census-demography-infrastructure/target/census-demography.jar
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

mvn clean package -f services/springboot/census-gateway
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

Grafana Dashboards:

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
