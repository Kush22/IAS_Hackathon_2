#!/bin/bash

# Compiling the program
javac -cp ".:amqp-client-4.0.2.jar:xmlParser.jar" ISMain.java

# Executing
java -cp ".:amqp-client-4.0.2.jar:slf4j-api-1.7.21.jar:slf4j-simple-1.7.22.jar:xmlParser.jar" ISMain
