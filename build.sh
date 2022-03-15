#!/bin/sh

rm *.jar
gradle build
cp lib/build/libs/blastengine-0.0.1-SNAPSHOT.jar .
