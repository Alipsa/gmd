#!/usr/bin/env bash
set -e
source ~/.sdkman/bin/sdkman-init.sh
source jdk21

./gradlew clean publishToSonatype closeAndReleaseSonatypeStagingRepository
echo "see https://oss.sonatype.org for more info"
echo "building the fatJar"
./gradlew fatJar