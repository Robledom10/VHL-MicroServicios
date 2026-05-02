FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY reservation-service/pom.xml reservation-service/pom.xml
COPY reservation-service/src reservation-service/src

RUN mvn -f reservation-service/pom.xml -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/reservation-service/target/reservation-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]
