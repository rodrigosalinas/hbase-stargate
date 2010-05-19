#!/bin/sh
mvn -q clean
mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=zookeeper -Dversion=3.2.2 -Dpackaging=jar -Dfile=lib/zookeeper-3.2.2.jar
mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hbase -Dversion=0.20.5 -Dpackaging=jar -Dfile=lib/hbase-0.20.5.jar
mvn install:install-file -DgroupId=org.apache.hadoop -DartifactId=hbase -Dversion=0.20.5 -Dclassifier=tests -Dpackaging=jar -Dfile=lib/hbase-0.20.5-test.jar
mvn install
