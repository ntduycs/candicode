#!/bin/bash

mvn clean package -Pprod -Dspring.profiles.active=prod -Dmaven.test.skip=true

scp -i ~/Desktop/no.pem ~/Desktop/java/candicode/target/candicode-1.0.jar ubuntu@ec2-54-169-117-15.ap-southeast-1.compute.amazonaws.com:~