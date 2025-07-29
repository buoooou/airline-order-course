#!/bin/bash
script_path=$(dirname $(readlink -f "$0"))
cd $script_path

# load environment parameters
. env.sh

# run test, using application-test.properties
./mvnw test -Dspring.profiles.active=test
