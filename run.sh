#!/bin/sh

# Compile and install the Java code
mvn clean install

# Run the application with the necessary MINGW argument
mvn exec:java -Dexec.mainClass='org.example.Main' -Dexec.args="--add-opens java.base/java.lang=ALL-UNNAMED"
