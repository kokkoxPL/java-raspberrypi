#!/bin/bash

cd "$(dirname "$0")"
nohup ${JAVA_HOME}/bin/java -jar /opt/gpio-testing/web-server.jar &
exit 0