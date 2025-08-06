# Multi-stage build para otimizar o tamanho da imagem
FROM maven:3.8.1-openjdk-11 AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de dependências primeiro (para cache)
COPY pom.xml .
COPY src ./src

# Baixar dependências e compilar
RUN mvn clean package -DskipTests

# Imagem de produção
FROM openjdk:11-jre-slim

# Definir diretório de trabalho
WORKDIR /app

# Copiar o JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Expor porta
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "app.jar"] 