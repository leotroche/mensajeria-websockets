# Etapa 1: Construcción del JAR
FROM eclipse-temurin:25-jdk-jammy AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a fat JAR with all dependencies
RUN mvn clean package -DskipTests

# Build a fat JAR with all dependencies
RUN ./mvnw clean package -DskipTests

# Etapa 2: Desarrollo
FROM eclipse-temurin:25-jdk-jammy AS development

WORKDIR /app
COPY --from=build /app/target/mensajeria-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "if [ \"$1\" = 'test' ]; then java -jar app.jar; else java -jar app.jar; fi", "--"]

# Etapa 3: Producción
FROM eclipse-temurin:25-jdk-jammy AS production

WORKDIR /app
COPY --from=build /app/target/mensajeria-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
