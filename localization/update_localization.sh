#!/bin/sh

echo "Compiling..."
javac -encoding utf8 LocalizedFilesGenerator.java

echo "Generating files..."

echo $'\nApp…'
java LocalizedFilesGenerator android 0 ../app/src/main/res/values/strings.xml ../app/src/main/res/values-fr/strings.xml

echo $'\nCleaning up'
find . -name "*.class" -type f -delete
