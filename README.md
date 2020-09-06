# Microservices Tutorial Springboot (Under Construction)

This is a tutorial project to show many approach used with microservices.

**Table of Contents**
* [About the tutorial](#about-the-tutorial)
* [Building, Testing and Running the springboot application](#building-testing-and-running-the-springboot-application)
* [Building, Testing and Running the springboot application with Consul](#building-testing-and-running-the-springboot-application-with-consul)
* [Building the images](#building-the-images)
* [Running the springboot application with Consul (docker mode)](#running-the-springboot-application-with-consul-docker-mode)
* [Running the springboot application with Consul and Kong (docker mode)](#running-the-springboot-application-with-consul-and-kong-docker-mode)
* [Building and Running the springboot application in Kubernetes with Istio](#building-and-running-the-springboot-application-in-kubernetes-with-istio)

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
mvn clean install -f services/springboot/internal-consul-utils

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

## Building the images

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

***

## Running the springboot application with Consul (docker mode)

* running the consul:
```
docker network create --driver=bridge --subnet=192.168.0.0/16 census-net

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

***

## Running the springboot application with Consul and Kong (docker mode)

* running the consul:
```
docker network create --driver=bridge --subnet=192.168.0.0/16 census-net

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

* preparing the kong environment:
```
docker run -d --name kong-database \
       --network=census-net \
       --ip=192.168.255.249 \
       -p 5432:5432 \
       -e "POSTGRES_USER=kong" \
       -e "POSTGRES_DB=kong" \
       -e "POSTGRES_PASSWORD=k0ngc0nsvl" \
       postgres:9.6 

docker run --rm -it \
       --network=census-net \
       -e "KONG_DATABASE=postgres" \
       -e "KONG_PG_HOST=kong-database" \
       -e "KONG_PG_PASSWORD=k0ngc0nsvl" \
       kong:2.0.4 kong migrations bootstrap

docker run --rm -it \
       --network=census-net \
       pantsel/konga -c prepare -a postgres -u postgresql://kong:k0ngc0nsvl@kong-database:5432/konga_db       
```

* running the gateway kong and kong admin:
```
docker-compose -f compose/docker-compose-springboot-kong.yml up
```

Kong Admin: [http://localhost:1337](http://localhost:1337)

`Kong Admin URL: http://tutorial-kong:8001`

* adding the kong rules:
```
-- ZipCode
curl -X POST http://localhost:8001/upstreams \
     --data "name=censuszipcode.upstream" \
     --data 'healthchecks.active.healthy.interval=5' \
     --data 'healthchecks.active.unhealthy.interval=5' \
     --data 'healthchecks.active.unhealthy.http_failures=5' \
     --data 'healthchecks.active.healthy.successes=5'     

curl -X POST http://localhost:8001/upstreams/censuszipcode.upstream/targets \
     --data "target=census-zipcode.service.consul:1411" \
     --data "weight=100"
curl -X POST http://localhost:8001/upstreams/censuszipcode.upstream/targets \
     --data "target=census-zipcode.service.consul:1412" \
     --data "weight=100"

curl -X POST http://localhost:8001/services/ \
     --data "name=censuszipcode.service" \
     --data "host=censuszipcode.upstream" \
     --data "path=/zipcode"

curl -X POST http://localhost:8001/services/censuszipcode.service/routes/ \
     --data "paths[]=/(?i)zipcode"

curl -X POST http://localhost:8001/services/censuszipcode.service/plugins \
     --data "name=prometheus" 

-- Demography
curl -X POST http://localhost:8001/upstreams \
     --data "name=censusdemography.upstream" \
     --data 'healthchecks.active.healthy.interval=5' \
     --data 'healthchecks.active.unhealthy.interval=5' \
     --data 'healthchecks.active.unhealthy.http_failures=5' \
     --data 'healthchecks.active.healthy.successes=5'     

curl -X POST http://localhost:8001/upstreams/censusdemography.upstream/targets \
     --data "target=census-demography.service.consul:1421" \
     --data "weight=100"
curl -X POST http://localhost:8001/upstreams/censusdemography.upstream/targets \
     --data "target=census-demography.service.consul:1422" \
     --data "weight=100"

curl -X POST http://localhost:8001/services/ \
     --data "name=censusdemography.service" \
     --data "host=censusdemography.upstream" \
     --data "path=/demography"

curl -X POST http://localhost:8001/services/censusdemography.service/routes/ \
     --data "paths[]=/(?i)demography"

curl -X POST http://localhost:8001/services/censusdemography.service/plugins \
     --data "name=prometheus" 

-- Census
curl -X POST http://localhost:8001/upstreams \
     --data "name=census.upstream" \
     --data 'healthchecks.active.healthy.interval=5' \
     --data 'healthchecks.active.unhealthy.interval=5' \
     --data 'healthchecks.active.unhealthy.http_failures=5' \
     --data 'healthchecks.active.healthy.successes=5'     

curl -X POST http://localhost:8001/upstreams/census.upstream/targets \
     --data "target=census.service.consul:1311" \
     --data "weight=100"
curl -X POST http://localhost:8001/upstreams/census.upstream/targets \
     --data "target=census.service.consul:1312" \
     --data "weight=100"

curl -X POST http://localhost:8001/services/ \
     --data "name=census.service" \
     --data "host=census.upstream" \
     --data "path=/census"

curl -X POST http://localhost:8001/services/census.service/routes/ \
     --data "paths[]=/(?i)census"

curl -X POST http://localhost:8001/services/census.service/plugins \
     --data "name=prometheus" 
```

Once the gateway is running, you can call:
```
curl http://localhost:8000/census/37188
curl http://localhost:8000/zipcode/37188
curl http://localhost:8000/demography/37188
```

* running the monitor:
```
docker-compose -f compose/docker-compose-springboot-monitor-kong.yml up
```

Links: [[Prometheus]](http://localhost:9090/) [[Grafana]](http://localhost:3000/)

Grafana Dashboards:

[https://grafana.com/grafana/dashboards/10642](https://grafana.com/grafana/dashboards/10642)

[https://grafana.com/grafana/dashboards/4701](https://grafana.com/grafana/dashboards/4701)

[https://grafana.com/grafana/dashboards/7424](https://grafana.com/grafana/dashboards/7424)

* running the logging:
```
docker-compose -f compose/docker-compose-springboot-logging.yml up
```

Link: [http://localhost:5601/](http://localhost:5601/)

* checking the memory
```
docker stats $(docker ps --format={{.Names}})
```

***

## Building and Running the springboot application in Kubernetes with Istio

* preparing the docker registry

```
mkdir -p /opt/docker/auth

docker run --rm --entrypoint htpasswd registry:2 -Bbn admin admin > /opt/docker/auth/htpasswd

docker run -d -p 5000:5000 --restart=always --name registry \
     -e REGISTRY_STORAGE_DELETE_ENABLED=true \
     -v /opt/docker/auth:/auth \
     -e "REGISTRY_AUTH=htpasswd" \
     -e "REGISTRY_AUTH_HTPASSWD_REALM=Registry Realm" \
     -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
     -e REGISTRY_HTTP_ADDR=0.0.0.0:5000 \
     registry:2
```

* building the microservices and pushing to the registry
```
mvn clean install -f services/springboot/parent
mvn clean install -f services/springboot/internal-shared
mvn clean install -f services/springboot/internal-rest-http

mvn clean package -f services/springboot/census
mvn clean package -f services/springboot/census-zipcode
mvn clean package -f services/springboot/census-demography

mvn -Ddocker.registry=localhost:5000 -Ddocker.username=admin -Ddocker.password=admin -DdockerfileName=DockerfileIstio \
    docker:build docker:push -f services/springboot/census/census-infrastructure

mvn -Ddocker.registry=localhost:5000 -Ddocker.username=admin -Ddocker.password=admin -DdockerfileName=DockerfileIstio \
    docker:build docker:push -f services/springboot/census-zipcode/census-zipcode-infrastructure

mvn -Ddocker.registry=localhost:5000 -Ddocker.username=admin -Ddocker.password=admin -DdockerfileName=DockerfileIstio \
    docker:build docker:push -f services/springboot/census-demography/census-demography-infrastructure
```

or execute the bash `buildDockerRegistry.sh` inside the directory `services/springboot`

* starting the minikube

```
minikube start --memory=8192 --cpus=4 --vm-driver=hyperkit --kubernetes-version=v1.18.6 --disk-size=30GB --insecure-registry='0.0.0.0/0'
minikube addons enable metrics-server
minikube addons enable ingress
```

* installing the Istio

```
# Istio 1.6.5
curl -L https://git.io/getLatestIstio | ISTIO_VERSION=1.6.5 sh -
cd istio-1.6.5
export PATH=$PWD/bin:$PATH

# install
istioctl profile list
istioctl install --set profile=demo
```

* get information from istio

```
kubectl get pod -n istio-system
kubectl get svc -n istio-system
kubectl --namespace istio-system top pods --containers

istioctl proxy-status

echo "Istio Services: $(minikube ip):$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')"
```

* defining the user and password to interact with the Registry

```
kubectl create secret docker-registry service-registry --namespace=census --docker-server=$(ipconfig getifaddr en0):5000 \
        --docker-username=admin --docker-password=admin
```

* deploying the sample

```
kubectl create -f kubernetes/plataform/namespace-census.json
kubectl label namespace census istio-injection=enabled --overwrite

kubectl create -f kubernetes/plataform/censuszipcode-service.yml
kubectl create -f kubernetes/plataform/censuszipcode-deployment.yml

kubectl create -f kubernetes/plataform/censusdemography-service.yml
kubectl create -f kubernetes/plataform/censusdemography-deployment.yml

kubectl create -f kubernetes/plataform/census-service.yml
kubectl create -f kubernetes/plataform/census-deployment.yml

kubectl create -f kubernetes/networking/census-gateway.yml
```

Once the gateway is running, you can call:

```
curl $(minikube ip):$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')/zipcode/37188
curl $(minikube ip):$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')/demography/37188
curl $(minikube ip):$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')/census/37188
```

* Istio tools

Jaeger

```
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 15032:16686
```

Link: [http://127.0.0.1:15032/](http://127.0.0.1:15032/)



Kiali

```
kubectl port-forward $(kubectl get pod -n istio-system -l app=kiali -o jsonpath='{.items[0].metadata.name}') -n istio-system 20001
```

Link: [http://127.0.0.1:20001/](http://127.0.0.1:20001/) -> admin:admin
