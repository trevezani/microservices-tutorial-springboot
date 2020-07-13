#!/bin/bash
mvn clean install -f parent
mvn clean install -f internal-shared
mvn clean install -f internal-rest-http
mvn clean install -f internal-consul-utils
mvn clean package -Pconsul -f census
mvn clean package -Pconsul -f census-zipcode
mvn clean package -Pconsul -f census-demography
mvn clean package -f census-gateway
