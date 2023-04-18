FROM openjdk:19-jdk

ARG JAR_FILE=./build/libs/scoop-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar
ENV	USE_PROFILE local

ENTRYPOINT ["java","-Dspring.profiles.active=${USE_PROFILE}", "-jar","/app.jar"]
