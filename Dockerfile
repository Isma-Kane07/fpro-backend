# ----------------------------
# Étape 1 : build avec Maven + JDK
# ----------------------------
FROM maven:3.9-eclipse-temurin-21 AS build

# Définir le dossier de travail
WORKDIR /app

# Copier le pom.xml et les sources
COPY pom.xml .
COPY src ./src

# Compiler le projet et créer le JAR, sans lancer les tests
RUN mvn clean package -DskipTests

# ----------------------------
# Étape 2 : image légère pour exécuter l'application
# ----------------------------
FROM eclipse-temurin:21-jre-alpine

# Dossier de travail dans le container
WORKDIR /app

# Copier le JAR construit depuis l'image précédente
COPY --from=build /app/target/facturation-app-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port que Spring Boot utilisera
EXPOSE 8080

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
