FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Собираем events-contract (зависимость для statistics-service)
COPY events-contract/pom.xml ./events-contract/
COPY events-contract/src ./events-contract/src
RUN cd events-contract && mvn clean install -DskipTests -B

# Копируем pom.xml для statistics-service и загружаем зависимости
COPY statistics-service/pom.xml ./statistics-service/
RUN cd statistics-service && mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY statistics-service/src ./statistics-service/src
RUN cd statistics-service && mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=builder /build/statistics-service/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar

USER javauser

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
