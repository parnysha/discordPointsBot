FROM openjdk:17-alpine
ARG JAR_FILE=target/springBotDiscord-0.0.1-SNAPSHOT.jar
WORKDIR /opt/a
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]