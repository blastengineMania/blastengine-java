#!/bin/sh

rm *.jar
rm lib/build/libs/*.jar
gradle shadowJar
cp lib/build/libs/*.jar .
mv *.jar blastengine.jar
# cp blastengine.jar ../sample/app/libs/blastengine.jar
