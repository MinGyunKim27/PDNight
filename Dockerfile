FROM openjdk:17-jdk-slim
RUN ["apt-get","update"]
RUN ["apt-get","install","curl","-y"]
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]