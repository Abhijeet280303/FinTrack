# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy dependency manifests first (better layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the JAR (skip tests for faster builds)
COPY src ./src
RUN mvn package -DskipTests -B

# ---- Stage 2: Run ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080     
# so the securiety groups are configured accordingly 

# Run with container-friendly JVM defaults
ENTRYPOINT ["java", "-jar", "app.jar"]
