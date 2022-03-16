#!/bin/sh

rm *.jar
rm lib/build/libs/*.jar
gradle shadowJar
cp lib/build/libs/*.jar .
cp *.jar ../sample/app/libs/blastengine.jar
