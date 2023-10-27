# First stage: complete build environment
FROM maven:3.9.4-sapmachine-21 AS builder

COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
#COPY config /usr/src/app

# package jar
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:21

# copy jar from the first stage
COPY --from=builder /usr/src/app/target/bitmex-stream-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/app/bitmex-stream-1.0-SNAPSHOT-jar-with-dependencies.jar
#COPY --from=builder /usr/src/app/config config

ARG _PROJECT_ID
ENV PROJECT_ID=${_PROJECT_ID}

ENTRYPOINT ["java", "-cp", "/usr/app/bitmex-stream-1.0-SNAPSHOT-jar-with-dependencies.jar", "com.BitmexStreamPublisher.BitmexWebsocketClient"]