#!/bin/bash
mvn clean install -f parent
mvn clean install -f internal-shared
mvn clean install -f internal-rest-http

mvn clean package -f census
mvn clean package -f census-zipcode
mvn clean package -f census-demography

mvn -Ddocker.registry=localhost:5000 -Ddocker.username=admin -Ddocker.password=admin -DdockerfileName=DockerfileIstio \
    docker:build docker:push -f census/census-infrastructure

mvn -DdockerfileName=DockerfileIstio docker:remove -f census/census-infrastructure

mvn -Ddocker.registry=localhost:5000 -Ddocker.username=admin -Ddocker.password=admin -DdockerfileName=DockerfileIstio \
    docker:build docker:push -f census-zipcode/census-zipcode-infrastructure

mvn -DdockerfileName=DockerfileIstio docker:remove -f census-zipcode/census-zipcode-infrastructure

mvn -Ddocker.registry=localhost:5000 -Ddocker.username=admin -Ddocker.password=admin -DdockerfileName=DockerfileIstio \
    docker:build docker:push -f census-demography/census-demography-infrastructure

mvn -DdockerfileName=DockerfileIstio docker:remove -f census-demography/census-demography-infrastructure

docker rmi $(docker images -f "dangling=true" -q)
