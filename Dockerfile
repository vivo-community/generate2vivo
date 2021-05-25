FROM adoptopenjdk/openjdk11:jre-11.0.9.1_1-alpine AS execute-env
WORKDIR /app
COPY ./target/datacitecommons2vivo-*.jar ./app.jar

#give dumb-init pid=1
RUN apk add dumb-init

#don't run container as root
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
RUN chown -R javauser:javauser /app
USER javauser

EXPOSE 9000
ENTRYPOINT ["dumb-init", "java", "-jar", "/app/app.jar"]