FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Собираем events-contract (зависимость для matchmaker-service)
COPY events-contract/pom.xml ./events-contract/
COPY events-contract/src ./events-contract/src
RUN cd events-contract && mvn clean install -DskipTests -B

# Копируем pom.xml для matchmaker-service и загружаем зависимости
COPY matchmaker-service/pom.xml ./matchmaker-service/
RUN cd matchmaker-service && mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY matchmaker-service/src ./matchmaker-service/src
RUN cd matchmaker-service && mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=builder /build/matchmaker-service/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar

USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
