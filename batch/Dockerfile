# Étape 1 : Construire l'application avec Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


# Étape 2 : Exécuter l'application avec OpenJDK
FROM openjdk:17-jdk-slim

# Install PostgreSQL client
RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/batch-0.0.1-SNAPSHOT.jar /app/batch-processing.jar
COPY src/main/resources/students200.csv /app/src/main/resources/students200.csv
COPY .env .env
COPY wait-for-postgres.sh /wait-for-postgres.sh
RUN chmod +x /wait-for-postgres.sh
EXPOSE 9090
CMD ["/wait-for-postgres.sh", "postgres", "java", "-jar", "batch-processing.jar"]

