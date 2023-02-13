FROM openjdk:19-jdk

ARG JAR_FILE=./build/libs/scoop-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
