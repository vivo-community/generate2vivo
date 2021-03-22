FROM maven:3.6.3-jdk-11-slim AS build-env

WORKDIR /app
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn clean install -Dmaven.test.skip=true && cp target/*.jar app.jar

#-------- NEXT STAGE -----------

FROM adoptopenjdk/openjdk11:jre-11.0.9.1_1-alpine AS execute-env
WORKDIR /app
COPY --from=build-env /app/app.jar ./app.jar

#give dumb-init pid=1
RUN apk add dumb-init

#don't run container as root
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
RUN chown -R javauser:javauser /app
USER javauser

EXPOSE 9000
ENTRYPOINT ["dumb-init", "java", "-jar", "/app/app.jar"]
