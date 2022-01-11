FROM maven:3.6.1-jdk-8-slim AS build
RUN mkdir -p workspace
WORKDIR workspace
COPY pom.xml /workspace
RUN mvn verify --fail-never
COPY src /workspace/src
RUN mvn -f pom.xml clean install -DskipTests=true

FROM adoptopenjdk/openjdk8:alpine-jre
EXPOSE 8080
COPY --from=build /workspace/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]