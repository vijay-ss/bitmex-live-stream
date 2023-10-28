# Bitmex Streaming Data Pipeline

This project is a real-time data streaming pipeline based on the Bitmex API/websocket forex/crypto platform. 
It is intended to be an end-to-end solution for ingesting and storing data on Google Cloud Platform.

## Technology/Tools
- Bitmex API & websocket interface
- Google Cloud Platform
  - Pub/Sub
  - Dataflow
  - Cloud Build
- Java
  - Maven build tools
- Docker

## Running Locally

## Load GCP credentials

There are a few methods, either by referencing a local SSH key or authenticating via browser.

`export GOOGLE_APPLICATION_CREDENTIALS=./{path-to-json}`

Alternatively:

`gcloud auth login`

Also be sure to set the project_id as an environment variable: `export PROJECT_ID={your-project-name}`

## Compiling Maven modules
This project leverages Apache Maven for managing dependencies of all submodules. In order to install dependencies,
compile and execute each module, run the following:

`mvn -pl bitmex-publisher -am clean install`
`mvn -pl bitmex-publisher -am compile`
`mvn exec:java -pl bitmex-publisher -Dexec.mainClass=BitmexWebsocketClient`

The steps above can be executed for each submodule, by replacing the name in each command.

## Build Docker image locally for testing

### Publisher
`docker build . -f bitmex-publisher/Dockerfile -t bitmex-publisher`
`docker run -it bitmex-publisher`

## Build application on Google Cloud Platform

`gcloud builds submit --config cloudbuild.yaml`
