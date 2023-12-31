# Bitmex Streaming Data Pipeline

This project is a real-time data streaming pipeline based on the Bitmex API/websocket forex/crypto platform. 
It is intended to be an end-to-end solution for ingesting and storing data on Google Cloud Platform.

## Architecture
![](img/bitmex_architecture.png)

## Technology/Tools
- Bitmex API & websocket interface
- Google Cloud Platform
  - PubSub
  - Dataflow
  - Cloud Build
  - Artifact Registry
- Java
  - Maven build tools
- Docker

## Methodology
1. Data Ingestion via websocket api, using containerized Java application
2. Publish data to PubSub topic 
3. Process data using Apache Beam/Dataflow service as a Subscriber
4. Stream insert processed data into BigQuery

## Running Locally

### Load GCP credentials

There are a few methods, either by referencing a local SSH key or authenticating via browser.
- `export GOOGLE_APPLICATION_CREDENTIALS=./{path-to-json}`

Alternatively:
- `gcloud auth login`

Also be sure to set the project_id as an environment variable via: 
- `export PROJECT_ID={your-project-name}` or `export PROJECT_ID=$(gcloud config get project)`

## Publisher

### Compile and run Maven module locally
- `mvn -pl bitmex-publisher -am clean install`
- `mvn -pl bitmex-publisher -am compile`
- `mvn exec:java -pl bitmex-publisher -Dexec.mainClass=BitmexWebsocketClient`

### Build Docker image locally for testing
- `docker build . -f bitmex-publisher/Dockerfile -t bitmex-publisher`
- `docker run -it bitmex-publisher`

### Build application on Google Cloud Platform

- `gcloud builds submit --config cloudbuild.yaml`

## Subscriber

### Compile and run Maven module locally
- `mvn -pl bitmex-subscriber -am clean install`
- `mvn -pl bitmex-subscriber -am compile`
- `mvn exec:java -pl bitmex-subscriber -Dexec.mainClass=BitmexPipeline -Dexec.args="--pubsubTopic=${PUBSUB_TOPIC}"`

### Build Dataflow flex-template for Apache Beam

- Set environment variables to be used for creating the template
```
export PROJECT_ID=$(gcloud config get project)
export PROJECT_NUMBER=$(gcloud projects list \
--sort-by=projectId --limit=1 --filter='PROJECT_ID:'$PROJECT_ID'' | sed '1d' | awk '{print $3}')
export SERVICE_ACCOUNT=$(gcloud iam service-accounts list \
 --filter "Compute Engine default service account" | sed '1d' | awk '{print $6}') \
export PUBSUB_TOPIC=$(gcloud pubsub topics list \
--filter="name.scope(projects):'${PROJECT_ID}'" --limit=1 | awk '{print $2}' | sed '1d')
```
*Note: it is also possible to set these variables manually, instead of using `gcloud` commands

- Package jar file for Dataflow Runner
```
mvn -pl bitmex-subscriber clean package \
  -Dexec.mainClass=com.mycompany.app.BitmexPipeline \
  -Dexec.args=" \
  --project=${PROJECT_ID} \
  --runner=DataflowRunner \
  --region=us-central1 \
  --streaming=true \
  --stagingLocation=gs://bmx_dataflow_templates/staging \
  --templateLocation=gs://bmx_dataflow_templates/templates \
  --serviceAccount=${SERVICE_ACCOUNT} \
  --pubsubTopic=${PUBSUB_TOPIC}
  "
```

- Build and push the flex-template in Artifact Registry:
```
gcloud dataflow flex-template build \
gs://bmx_dataflow_templates/templates/dataflow-template.json \
--image-gcr-path="us-central1-docker.pkg.dev/${PROJECT_ID}/bitmex-dataflow/dataflow:latest" \
--sdk-language=JAVA \
--flex-template-base-image=JAVA11 \
--jar="bitmex-subscriber/target/bitmex-subscriber-1.0-SNAPSHOT.jar" \
--env FLEX_TEMPLATE_JAVA_MAIN_CLASS="com.mycompany.app.BitmexPipeline"
```

- Run the Dataflow job using the newly created flex-template
```
gcloud dataflow flex-template run "bitmex-pipeline" \
--template-file-gcs-location="gs://bmx_dataflow_templates/templates/dataflow-template.json" \
--region=us-central1 \
--parameters=pubsubTopic=${PUBSUB_TOPIC}
```

## References/Documentation
1. Bitmex websocket documentation: https://www.bitmex.com/app/wsAPI#Rate-Limits
2. Creating classic dataflow templates: https://cloud.google.com/dataflow/docs/guides/templates/creating-templates
3. Setting pipeline options: https://cloud.google.com/dataflow/docs/guides/setting-pipeline-options
4. Docker multi-module Maven project: https://stackoverflow.com/questions/51679363/multi-module-maven-project-on-dockers
5. maven-exec:java goal on multi-module project: https://stackoverflow.com/questions/11091311/maven-execjava-goal-on-a-multi-module-project
6. Maven Assembly Plugin: https://maven.apache.org/plugins/maven-assembly-plugin/usage.html
7. https://github.com/tosun-si/teams-league-java-standard-beam
8. https://mehmandarov.com/beam-pipeline-in-four-steps/
9. [Build and Deploy an Apache Beam pipeline using Flex-Template](https://www.youtube.com/watch?v=gwLnrAY_Udo&list=PLZWkpQ-uRAyzw8zn7A5iBQCN0kyBh6Kqo&index=6)