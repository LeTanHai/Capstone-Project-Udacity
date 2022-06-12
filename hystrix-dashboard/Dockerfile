FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} hystrix-dashboard.jar

EXPOSE 9295

ENTRYPOINT ["java","-jar","/hystrix-dashboard.jar"]