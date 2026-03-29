#!/bin/bash

echo "Compiling source files..."
javac -d out src/com/leandre/exception/*.java src/com/leandre/validation/*.java src/com/leandre/transaction/*.java src/com/leandre/customer/*.java src/com/leandre/storage/*.java src/com/leandre/account/*.java src/com/leandre/concurrent/*.java src/com/leandre/cli/*.java src/com/leandre/Main.java

echo "Compiling test files..."
javac -cp lib/junit-platform-console-standalone-1.10.2.jar:src -d out/test test/com/leandre/account/AccountTest.java

echo "Running tests..."
java -jar lib/junit-platform-console-standalone-1.10.2.jar execute --class-path out:out/test --scan-class-path
