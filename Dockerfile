FROM openjdk:17.0.2-jdk-slim as runtime

ENV APP_HOME /app
COPY . $APP_HOME
WORKDIR $APP_HOME
RUN ./mvnw clean install -U -DskipTests
EXPOSE 8080
ENTRYPOINT /usr/local/openjdk-17/bin/java -jar ./target/crmservice-0.0.1-SNAPSHOT.jar