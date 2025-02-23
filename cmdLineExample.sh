#!/usr/bin/env bash
set -e
if [[ ! -f build/libs/gmd-bundled-2.1.0-SNAPSHOT.jar ]]; then
  ./gradlew fatJar
fi
java -jar build/libs/gmd-bundled-2.1.0-SNAPSHOT.jar toPdf src/test/resources/test.gmd Test.pdf