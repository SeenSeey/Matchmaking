FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Собираем events-contract (зависимость для audit-service)
COPY events-contract/pom.xml ./events-contract/
COPY events-contract/src ./events-contract/src
RUN cd events-contract && mvn clean install -DskipTests -B

# Копируем pom.xml для audit-service и загружаем зависимости
COPY audit-service/pom.xml ./audit-service/
RUN cd audit-service && mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY audit-service/src ./audit-service/src
RUN cd audit-service && mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=builder /build/audit-service/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar

USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
