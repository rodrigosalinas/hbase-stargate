#!/bin/sh
mvn -q clean
mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hbase -Dversion=0.21.0-SNAPSHOT -Dpackaging=jar -Dfile=lib/hbase-0.21.0-SNAPSHOT.jar
mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hbase -Dversion=0.21.0-SNAPSHOT -Dclassifier=tests -Dpackaging=jar -Dfile=lib/hbase-0.21.0-SNAPSHOT-tests.jar
mvn install
