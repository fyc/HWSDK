#!/usr/bin/env bash
echo sdk.dir=$ANDROID_HOME > local.properties
gradle clean


cd container
gradle build
gradle build
cd ../app
gradle build

cd ..
bash ./package.sh