FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java","-jar","/app.jar"]
