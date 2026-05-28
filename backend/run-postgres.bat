@echo off
set "MAVEN_EXE=C:\Program Files\GIGA IDE\GIGA IDE Community Edition 2024.3\plugins\maven\lib\maven3\bin\mvn.cmd"

if not exist "%MAVEN_EXE%" (
  echo Maven was not found at:
  echo %MAVEN_EXE%
  echo Install Maven manually or update this script.
  exit /b 1
)

call "%MAVEN_EXE%" -Dmaven.test.skip=true spring-boot:run -Dspring-boot.run.profiles=postgres

