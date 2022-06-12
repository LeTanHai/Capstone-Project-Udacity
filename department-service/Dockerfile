FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} department-service.jar

EXPOSE 9001

ENTRYPOINT ["java","-jar","/department-service.jar"]