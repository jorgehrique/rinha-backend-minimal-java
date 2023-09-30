FROM maven:3.9.4-amazoncorretto-21 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM amazoncorretto:21-alpine-jdk

COPY --from=build /home/app/target/rinha-java-1.0-jar-with-dependencies.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]