FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/once-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar

RUN apk add tzdata

ENTRYPOINT ["java","-jar","/app.jar"]
