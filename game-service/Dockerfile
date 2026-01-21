FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Собираем events-contract (зависимость для game-service)
COPY events-contract/pom.xml ./events-contract/
COPY events-contract/src ./events-contract/src
RUN cd events-contract && mvn clean install -DskipTests -B

# Копируем pom.xml для game-service и загружаем зависимости
COPY game-service/pom.xml ./game-service/
RUN cd game-service && mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY game-service/src ./game-service/src
RUN cd game-service && mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=builder /build/game-service/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar

USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
