#!/bin/bash

mvn clean package -Dspring.profiles.active="$1"

mvn spring-boot:run