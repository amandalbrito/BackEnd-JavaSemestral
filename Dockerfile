# Usa imagem com Java 17
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o .jar do projeto compilado para dentro do container
COPY target/*.jar app.jar

# Comando para rodar o Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
