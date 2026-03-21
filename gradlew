#!/bin/bash

# Gradle wrapper script
# This script downloads and executes the Gradle wrapper

set -e

GRADLE_VERSION="8.5"
GRADLE_WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"
GRADLE_WRAPPER_PROPERTIES="gradle/wrapper/gradle-wrapper.properties"

# Check if wrapper jar exists, if not download it
if [ ! -f "$GRADLE_WRAPPER_JAR" ]; then
    echo "Downloading Gradle wrapper..."
    mkdir -p gradle/wrapper
    curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o gradle-bin.zip
    unzip -q gradle-bin.zip
    cp "gradle-${GRADLE_VERSION}/lib/gradle-launcher-${GRADLE_VERSION}.jar" "$GRADLE_WRAPPER_JAR" 2>/dev/null || true
    rm -rf "gradle-${GRADLE_VERSION}" gradle-bin.zip
fi

# Execute Gradle
exec java -Xmx2048m -Dorg.gradle.appname=gradlew -classpath "$GRADLE_WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
