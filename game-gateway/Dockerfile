FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Собираем events-contract (зависимость для game-gateway)
COPY events-contract/pom.xml ./events-contract/
COPY events-contract/src ./events-contract/src
RUN cd events-contract && mvn clean install -DskipTests -B

# Копируем pom.xml для game-gateway и загружаем зависимости
COPY game-gateway/pom.xml ./game-gateway/
RUN cd game-gateway && mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY game-gateway/src ./game-gateway/src
RUN cd game-gateway && mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=builder /build/game-gateway/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar

USER javauser

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
