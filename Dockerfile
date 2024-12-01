# Usando uma imagem base do OpenJDK
FROM openjdk:17-jdk-slim

# Definindo o diretório de trabalho dentro do container
WORKDIR /app

# Copiando o arquivo JAR gerado para o diretório de trabalho
COPY target/MarkCollabBackend-1.0.0.jar /app/MarkCollabBackend-1.0.0.jar

# Expondo a porta onde a aplicação vai rodar
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "MarkCollabBackend-1.0.0.jar"]
