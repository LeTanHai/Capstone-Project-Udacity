FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} user-service.jar

EXPOSE 9002

ENTRYPOINT ["java","-jar","/user-service.jar"]