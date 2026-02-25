# Etapa 1: Construcción del JAR
FROM maven:3.9.6-eclipse-temurin-25 AS build

WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Imagen para desarrollo y tests
FROM maven:3.9.6-eclipse-temurin-25 AS development

WORKDIR /app

# Copiar el código fuente y el .jar
COPY --from=build /app/target/mensajeria.jar app.jar
COPY --from=build /app/src ./src
COPY --from=build /app/pom.xml .

# Exponer el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar tests o la aplicación
ENTRYPOINT ["sh", "-c", "if [ \"$1\" = 'test' ]; then mvn -B clean verify; else java -jar app.jar; fi", "--"]

# Etapa 3: Imagen ligera para producción
FROM openjdk:25-jdk-slim AS production

WORKDIR /app

# Copiar el .jar generado en la etapa anterior
COPY --from=build /app/target/mensajeria.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8080

# Ejecutar la aplicación
CMD ["java", "--enable-preview", "-jar", "app.jar"]
