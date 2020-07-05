#!/bin/bash

mvn clean package -Dspring.profiles.active="$1" -Dmaven.test.skip-true

mvn spring-boot:run