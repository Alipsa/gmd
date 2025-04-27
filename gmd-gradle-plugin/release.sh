#!/usr/bin/env bash
set -e
./gradlew clean build publishPlugins --validate-only
./gradlew publishPlugins