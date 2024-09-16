@echo off
setlocal

:: Set GitHub repository and token
set GITHUB_REPO=WannaBeIan/ZephyriteCore
set GITHUB_TOKEN=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

:: Get project version from Maven
for /f "tokens=*" %%i in ('mvn help:evaluate "-Dexpression=project.version" -q -DforceStdout ^| findstr "^[0-9]"') do (
    set VERSION=%%i
)

:: Ensure the version was extracted
if "%VERSION%"=="" (
    echo Error: Could not extract version from pom.xml
    exit /b 1
)

echo Project version is: %VERSION%

:: Check if the JAR file exists
set JAR_FILE=target\ZephyriteCore-%VERSION%.jar
if exist "%JAR_FILE%" (
    echo JAR file found: %JAR_FILE%
) else (
    echo Error: JAR file not found. Expected %JAR_FILE%
    exit /b 1
)

:: Create GitHub release
echo Creating GitHub release for version %VERSION%...
curl -L -X POST ^
  -H "Accept: application/vnd.github+json" ^
  -H "Authorization: Bearer %GITHUB_TOKEN%" ^
  -H "X-GitHub-Api-Version: 2022-11-28" ^
  https://api.github.com/repos/%GITHUB_REPO%/releases ^
  -d "{\"tag_name\":\"v%VERSION%\",\"target_commitish\":\"master\",\"name\":\"v%VERSION%\",\"body\":\"Release for ZephyriteCore version %VERSION%.\",\"draft\":false,\"prerelease\":false,\"generate_release_notes\":false}" > release.json

:: Print release.json to inspect the response
type release.json

:: Extract upload URL from JSON response
for /f "tokens=*" %%i in ('jq -r ".upload_url" release.json') do (
    set UPLOAD_URL=%%i
)

:: Ensure the upload URL is valid
if "%UPLOAD_URL%"=="" (
    echo Error: Could not extract the upload URL.
    exit /b 1
)

:: Clean up the upload URL
set UPLOAD_URL=%UPLOAD_URL:{?name,label}=%

echo Uploading JAR file to release...
curl -X POST -H "Authorization: Bearer %GITHUB_TOKEN%" ^
    -H "Content-Type: application/octet-stream" ^
    --data-binary @"target/ZephyriteCore-%VERSION%.jar" ^
    "%UPLOAD_URL%?name=ZephyriteCore-%VERSION%.jar"

echo Plugin JAR uploaded to GitHub release.

endlocal
exit /b 0
