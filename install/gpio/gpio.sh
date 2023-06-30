#!/bin/bash

cd "$(dirname "$0")"
nohup ${JAVA_HOME}/bin/java -jar /opt/gpio-testing/gpio.jar &
exit 0