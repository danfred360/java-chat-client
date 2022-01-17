#!/bin/bash

echo "cleaning maven project..."
mvn package clean

echo "building maven project..."
mvn package

echo "starting client..."
java -jar target/iglooclient-1.0-SNAPSHOT.jar 