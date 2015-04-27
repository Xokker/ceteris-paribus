#!/bin/sh

mvn exec:java -Dexec.mainClass=com.xokker.datasets.cars.Cars1 \
    -Dorg.slf4j.simpleLogger.defaultLogLevel=info \
    -Dorg.slf4j.simpleLogger.log.com.xokker.CeterisParibus=debug \
    -Dorg.slf4j.simpleLogger.showThreadName=false


#-Dorg.slf4j.simpleLogger.logFile=file.log
