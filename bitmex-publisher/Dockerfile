# First stage: complete build environment
FROM maven:3.9.4-sapmachine-21 AS builder

# Copy parent pom
COPY pom.xml /pom.xml

COPY bitmex-publisher bitmex-publisher
#COPY config /usr/src/app

# package jar
RUN mvn -f bitmex-publisher/pom.xml clean package

FROM openjdk:21

# copy jar from the first stage
COPY --from=builder /bitmex-publisher/target/bitmex-publisher-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/app/bitmex-publisher-1.0-SNAPSHOT-jar-with-dependencies.jar
#COPY --from=builder /usr/src/app/config config

ARG _PROJECT_ID
ENV PROJECT_ID=${_PROJECT_ID}

ENTRYPOINT ["java", "-cp", "/usr/app/bitmex-publisher-1.0-SNAPSHOT-jar-with-dependencies.jar", "com.mycompany.app.BitmexWebsocketClient"]