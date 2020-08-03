#!/bin/bash

mvn clean package -Pprod -Dspring.profiles.active=prod -Dmaven.test.skip=true

scp -i ~/Desktop/no.pem ~/Desktop/java/candicode/target/candicode-1.0.jar ubuntu@13.212.4.81:~
