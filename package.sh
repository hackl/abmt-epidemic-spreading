#!/bin/bash
# create temp folder
mkdir temp/

# remove folder if exist
rm -rf temp/source/

# make mvn package
mvn package

# unzip package
unzip target/project-1.0-SNAPSHOT-release.zip -d temp/

# rename folder
mv temp/project-1.0-SNAPSHOT/ temp/source/
