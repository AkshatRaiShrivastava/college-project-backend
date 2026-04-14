# Multi-stage build for Spring Boot app on Render
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Layer 1: base build files and dependencies
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Layer 2: source and package
COPY src src
RUN ./mvnw -B -DskipTests package
RUN JAR_FILE=$(find target -maxdepth 1 -name "*.jar" ! -name "*original*" | head -n 1) && cp "$JAR_FILE" /app/app.jar

# Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/app.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-Dserver.port=${PORT}","-jar","/app/app.jar"]
