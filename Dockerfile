FROM maven:3.6.3-jdk-11-slim AS build-env

WORKDIR /app
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn clean install -Dmaven.test.skip=true && cp target/*.jar app.jar

#-------- NEXT STAGE -----------

FROM openjdk:11-jre-slim AS execute-env

WORKDIR /app
COPY --from=build-env /app/app.jar ./app.jar
EXPOSE 9000
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
