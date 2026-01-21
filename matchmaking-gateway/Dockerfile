FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Собираем events-contract (зависимость для matchmaking-gateway)
COPY events-contract/pom.xml ./events-contract/
COPY events-contract/src ./events-contract/src
RUN cd events-contract && mvn clean install -DskipTests -B

# Собираем matchmaking-api (зависимость для matchmaking-gateway)
COPY matchmaking-api/pom.xml ./matchmaking-api/
COPY matchmaking-api/src ./matchmaking-api/src
RUN cd matchmaking-api && mvn clean install -DskipTests -B && \
    echo "Checking if schema.graphqls is in JAR:" && \
    jar tf target/matchmaking-api-1.0.jar | grep -i "graphql\|schema" || echo "WARNING: Schema not found in JAR"

# Копируем pom.xml для matchmaking-gateway и загружаем зависимости
COPY matchmaking-gateway/pom.xml ./matchmaking-gateway/
RUN cd matchmaking-gateway && mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY matchmaking-gateway/src ./matchmaking-gateway/src
RUN cd matchmaking-gateway && mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=builder /build/matchmaking-gateway/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar

USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
