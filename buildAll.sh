#!/usr/bin/env bash
set -e

localRepo=$(mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
if [[ -d "$localRepo/se/alipsa/gmd" ]]; then
  echo "Removing local cache in $localRepo/se/alipsa/gmd"
  rm -r "$localRepo/se/alipsa/gmd"
fi
pushd gmd-core
  ./gradlew clean build publishToMavenLocal
popd

pushd GmdTestGui
  ./gradlew clean build
popd

pushd gmd-gradle-plugin
  ./gradlew clean build publishToMavenLocal
  pushd src/test/manual-test
    ./gradlew clean build
  popd
popd

pushd gmd-maven-plugin
  mvn clean install
  pushd src/test/projects/manual
    mvn clean verify
  popd
popd
echo "Done"