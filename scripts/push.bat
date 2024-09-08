#!/bin/bash

# Get the project version from Maven
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Project version: $VERSION"

# Navigate to the target directory
cd target || { echo "Failed to cd into target"; exit 1; }

# Check if the JAR exists
if [[ -f ZephyriteCore-${VERSION}-shaded.jar ]]; then
  echo "JAR file found, proceeding with rename..."
  mv ZephyriteCore-${VERSION}-shaded.jar ZephyriteCore-latest.jar
else
  echo "JAR file not found: ZephyriteCore-${VERSION}-shaded.jar"
  exit 1
fi

# Add, commit, and push to GitHub with versioning in the commit message
git add ZephyriteCore-latest.jar
git commit -m "Updated ZephyriteCore plugin to version ${VERSION}"
git push origin main || { echo "Failed to push to GitHub"; exit 1; }

echo "Plugin JAR renamed to ZephyriteCore-latest.jar and pushed to GitHub."
