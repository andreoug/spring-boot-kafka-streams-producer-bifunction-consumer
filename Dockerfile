FROM maven:3.8-openjdk-17-slim AS build
RUN mkdir -p workspace
WORKDIR workspace
COPY pom.xml /workspace
RUN mvn verify --fail-never
COPY src /workspace/src
RUN mvn -f pom.xml clean install -DskipTests=true

FROM openjdk:17-alpine
RUN apk add --no-cache libstdc++
EXPOSE 8080
COPY --from=build /workspace/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]