#!/bin/sh

rm *.jar
rm lib/build/libs/*.jar
gradle shadowJar
cp blastengine-included.jar ../sample/app/libs/blastengine.jar
