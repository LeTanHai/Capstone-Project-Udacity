FROM openjdk:11

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} cloud-config-server.jar

EXPOSE 9296

ENTRYPOINT ["java","-jar","/cloud-config-server.jar"]