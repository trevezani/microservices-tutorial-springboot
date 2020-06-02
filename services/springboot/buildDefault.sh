#!/bin/bash
mvn clean install -f parent
mvn clean install -f internal-shared
mvn clean install -f internal-rest-http
mvn clean package -f census
mvn clean package -f census-zipcode
mvn clean package -f census-demography
