FROM gradle:8.6-jdk21
LABEL authors="viva"

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} /jwt-filter-again.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=default", "/jwt-filter-again.jar"]