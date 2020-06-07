#!/bin/bash
mvn clean install -f parent
mvn clean install -f internal-shared
mvn clean install -f internal-rest-http
mvn clean package -Pconsul -f census
mvn clean package -Pconsul -f census-zipcode
mvn clean package -Pconsul -f census-demography
mvn clean package -f census-gateway

docker rmi census:0.0.1-SNAPSHOT
docker rmi census-zipcode:0.0.1-SNAPSHOT
docker rmi census-demography:0.0.1-SNAPSHOT
docker rmi census-gateway:0.0.1-SNAPSHOT

mvn docker:build -f census/census-infrastructure
mvn docker:build -f census-zipcode/census-zipcode-infrastructure
mvn docker:build -f census-demography/census-demography-infrastructure
mvn docker:build -f census-gateway

docker rmi $(docker images -f "dangling=true" -q)
