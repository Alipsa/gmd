#!/usr/bin/env bash
set -e
# for convenience, parse the version number from the build.gradle file
VERSION=$(grep GMD_version_mark < build.gradle | awk '{ print $3 }')
VERSION="${VERSION//\'}"
# echo "Version: $VERSION"
if [[ ! -f build/libs/gmd-bundled-$VERSION.jar ]]; then
  ./gradlew fatJar
fi
java -jar build/libs/gmd-bundled-$VERSION.jar toPdf src/test/resources/test.gmd build/cmdLineExample.pdf