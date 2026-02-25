# ------------------------------
# Stage 1: Build the fat JAR
# ------------------------------
FROM eclipse-temurin:25-jdk-jammy AS build

# Install Maven
RUN apt-get update && apt-get install -y maven

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build a fat JAR with all dependencies
RUN mvn clean package -DskipTests

# ------------------------------
# Stage 2: Development
# ------------------------------
FROM eclipse-temurin:25-jdk-jammy AS development

# Install Maven so tests can run
RUN apt-get update && apt-get install -y maven

WORKDIR /app

# Copy the built fat JAR
COPY --from=build /app/target/mensajeria-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Entry point: if "test" argument passed, run Maven tests; otherwise run app
ENTRYPOINT ["sh", "-c", "if [ \"$1\" = 'test' ]; then mvn -B clean verify; else java -jar app.jar; fi", "--"]

# ------------------------------
# Stage 3: Production
# ------------------------------
FROM eclipse-temurin:25-jdk-jammy AS production

WORKDIR /app

# Copy the built fat JAR
COPY --from=build /app/target/mensajeria-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
