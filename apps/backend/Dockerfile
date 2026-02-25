# Etapa 1: Construcción del JAR
FROM eclipse-temurin:25-jdk-jammy AS build

# Install Maven manually
RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


# Etapa 2: Desarrollo
FROM eclipse-temurin:25-jdk-jammy AS development

RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY --from=build /app/target/mensajeria.jar app.jar
COPY --from=build /app/src ./src
COPY --from=build /app/pom.xml .

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "if [ \"$1\" = 'test' ]; then mvn -B clean verify; else java -jar app.jar; fi", "--"]


# Etapa 3: Producción
FROM eclipse-temurin:25-jdk-jammy AS production

WORKDIR /app
COPY --from=build /app/target/mensajeria.jar app.jar

EXPOSE 8080
CMD ["java", "--enable-preview", "-jar", "app.jar"]
