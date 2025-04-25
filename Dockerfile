#building
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

COPY src src
COPY .env .
RUN ./mvnw package

# containerizing
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/transaction-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/.env .

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]