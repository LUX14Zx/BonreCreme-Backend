FROM maven:3.9.6-eclipse-temurin-21 AS build
LABEL authors="LUX14Zx"


WORKDIR /app
# Copy Maven descriptor and source code
COPY pom.xml .
COPY src ./src

# Build the application without running tests
RUN mvn -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=production"]