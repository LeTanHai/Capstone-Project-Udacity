FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} cloud-gateway.jar

EXPOSE 9191

ENTRYPOINT ["java","-jar","/cloud-gateway.jar"]