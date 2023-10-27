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

## Build docker image locally for testing

`docker build -t bitmex-stream .`
`docker run -it bitmex-stream`

## Build application on Google Cloud Platform

`gcloud builds submit --config cloudbuild.yaml`
