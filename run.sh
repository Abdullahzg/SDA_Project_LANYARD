#!/bin/sh

# Compile the Java code
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="org.example.Main"