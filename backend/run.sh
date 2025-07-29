#!/bin/bash
script_path=$(dirname $(readlink -f "$0"))
cd $script_path

# load environment parameters
. env.sh

# define jar path
JAR_PATH=./target/airline-order-backend-0.0.1-SNAPSHOT.jar

# run application, using application-dev.properties
java -jar ${JAR_PATH} --spring.profiles.active=dev
