#!/usr/bin/env bash
set -e
./gradlew clean build publishToMavenLocal
pushd src/test/manual-test
  ./gradlew clean build
popd
./gradlew clean build publishPlugins --validate-only
./gradlew publishPlugins