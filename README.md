# java-access-token
Purpose: to generate access token from java code, instead of running
gcloud auth print-access-token
## need to create a Service Account json key file

## maven build command:
mvn clean compile assembly:single

## test build:
java -jar target/authsamples-1.0.0-jar-with-dependencies.jar

it will output access ID token