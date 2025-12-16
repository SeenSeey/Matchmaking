FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Собираем events-contract (зависимость для matchmaking-rest)
COPY events-contract/pom.xml ./events-contract/
COPY events-contract/src ./events-contract/src
RUN cd events-contract && mvn clean install -DskipTests -B

# Собираем matchmaking-api (зависимость для matchmaking-rest)
COPY matchmaking-api/pom.xml ./matchmaking-api/
COPY matchmaking-api/src ./matchmaking-api/src
RUN cd matchmaking-api && mvn clean install -DskipTests -B

# Копируем pom.xml для matchmaking-rest и загружаем зависимости
COPY matchmaking-rest/pom.xml ./matchmaking-rest/
RUN cd matchmaking-rest && mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY matchmaking-rest/src ./matchmaking-rest/src
RUN cd matchmaking-rest && mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=builder /build/matchmaking-rest/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar

USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

